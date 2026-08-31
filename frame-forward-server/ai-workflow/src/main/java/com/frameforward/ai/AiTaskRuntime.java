package com.frameforward.ai;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.auth.AuthService;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class AiTaskRuntime {
  private static final Logger log = LoggerFactory.getLogger(AiTaskRuntime.class);
  public enum State { QUEUED, RUNNING, SUCCEEDED, FAILED, CANCELLED }
  private final AiTaskMapper tasks; private final ObjectMapper json; private final AuthService auth; private final Executor executor; private final SceneAnalysisGraph sceneGraph; private final PlanGenerationGraph planGraph; private final PhotoEvaluationGraph evaluationGraph; private final ReferenceImageGraph referenceImageGraph; private final List<AiTaskCompletionProcessor> completionProcessors;
  private final String defaultModel, coachModel, sceneModel, imageModel; private final Map<String, List<SseEmitter>> subscribers = new ConcurrentHashMap<>();
  public AiTaskRuntime(AiTaskMapper tasks, ObjectMapper json, AuthService auth, @Qualifier("applicationTaskExecutor") Executor executor,
      @Value("${frame-forward.ai.qwen-routes.default:qwen-plus}") String defaultModel,
      @Value("${frame-forward.ai.qwen-routes.coach:qwen-plus}") String coachModel,
      @Value("${frame-forward.ai.qwen-routes.scene:qwen3.8-max}") String sceneModel, @Value("${frame-forward.ai.qwen-routes.image:qwen-image-3.0-pro}") String imageModel, SceneAnalysisGraph sceneGraph, PlanGenerationGraph planGraph, PhotoEvaluationGraph evaluationGraph, ReferenceImageGraph referenceImageGraph, List<AiTaskCompletionProcessor> completionProcessors) { this.tasks=tasks; this.json=json; this.auth=auth; this.executor=executor; this.defaultModel=defaultModel; this.coachModel=coachModel; this.sceneModel=sceneModel; this.imageModel=imageModel; this.sceneGraph=sceneGraph; this.planGraph=planGraph; this.evaluationGraph=evaluationGraph; this.referenceImageGraph=referenceImageGraph; this.completionProcessors=completionProcessors; }
  @PostConstruct @Transactional public void recoverInterruptedTasks() { for (var task : tasks.selectList(new LambdaQueryWrapper<AiTaskEntity>().eq(AiTaskEntity::getState, State.RUNNING.name()))) { task.state=State.QUEUED.name(); task.updatedAt=Instant.now(); tasks.updateById(task); } }
  public Created create(String accessToken, String key, CreateRequest request) {
    if (key == null || key.isBlank() || key.length() > 128 || request == null || request.operationType == null || request.operationType.isBlank()) throw new BadRequest();
    var accountId = auth.requireAccountId(accessToken); var existing = tasks.selectOne(new LambdaQueryWrapper<AiTaskEntity>().eq(AiTaskEntity::getAccountId, accountId).eq(AiTaskEntity::getOperationType, request.operationType).eq(AiTaskEntity::getIdempotencyKey, key));
    if (existing != null) return new Created(existing.id, State.valueOf(existing.state));
    var task = new AiTaskEntity(); task.id=UUID.randomUUID().toString(); task.accountId=accountId; task.operationType=request.operationType; task.idempotencyKey=key; task.state=State.QUEUED.name(); task.workflowVersion="v1"; task.modelId=modelFor(request.operationType); task.promptVersion="v1"; task.ruleVersion="v1"; task.schemaVersion="v1"; task.inputJson=write(request.input == null ? Map.of() : request.input); task.createdAt=task.updatedAt=Instant.now();
    try { tasks.insert(task); } catch (DuplicateKeyException duplicate) { var found=tasks.selectOne(new LambdaQueryWrapper<AiTaskEntity>().eq(AiTaskEntity::getAccountId, accountId).eq(AiTaskEntity::getOperationType, request.operationType).eq(AiTaskEntity::getIdempotencyKey, key)); return new Created(found.id, State.valueOf(found.state)); }
    schedule(task.id); return new Created(task.id, State.QUEUED);
  }
  public Status get(String accessToken, String id) { var task=owned(accessToken,id); return status(task); }
  public SseEmitter events(String accessToken, String id) { var task=owned(accessToken,id); var emitter=new SseEmitter(0L); subscribers.computeIfAbsent(id, ignored -> new ArrayList<>()).add(emitter); emitter.onCompletion(() -> subscribers.getOrDefault(id,List.of()).remove(emitter)); emit(emitter, status(task)); return emitter; }
  void run(String id) { var task=tasks.selectById(id); if (task == null || !transition(task,State.QUEUED,State.RUNNING)) return; try { for (int attempt=0; attempt<3; attempt++) { Map<String,Object> output=outputFor(task,attempt); if (valid(task.operationType, output)) { completionProcessors.stream().filter(processor -> processor.supports(task.operationType)).forEach(processor -> processor.complete(task, output)); task.resultJson=write(output); task.errorCode=null; transition(task,State.RUNNING,State.SUCCEEDED); return; } } task.resultJson=null; task.errorCode="INVALID_MODEL_OUTPUT"; transition(task,State.RUNNING,State.FAILED); } catch (RuntimeException exception) { log.warn("AI task {} failed during {}", task.id, task.operationType, exception); task.resultJson=null; task.errorCode="RUNTIME_ERROR"; transition(task,State.RUNNING,State.FAILED); } }
  private boolean transition(AiTaskEntity task, State from, State to) { if (State.valueOf(task.state)!=from || !legal(from,to)) return false; task.state=to.name(); task.updatedAt=Instant.now(); tasks.updateById(task); publish(status(task)); return true; }
  private void schedule(String taskId) { if (TransactionSynchronizationManager.isActualTransactionActive()) TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() { @Override public void afterCommit() { executor.execute(() -> run(taskId)); } }); else executor.execute(() -> run(taskId)); }
  static boolean legal(State from, State to) { return (from==State.QUEUED && (to==State.RUNNING||to==State.CANCELLED)) || (from==State.RUNNING && (to==State.SUCCEEDED||to==State.FAILED||to==State.CANCELLED)); }
  private Map<String,Object> outputFor(AiTaskEntity task, int attempt) { Map<String,Object> input=read(task.inputJson); if ("scene-analysis".equals(task.operationType)) return sceneGraph.execute(input); if ("shooting-plan-generation".equals(task.operationType)) return planGraph.execute(input); if ("photo-evaluation".equals(task.operationType)) return evaluationGraph.execute(input); if ("reference-image-generation".equals(task.operationType)) return referenceImageGraph.execute(input); Object supplied=input.get("mockOutput"); if (supplied instanceof Map<?,?> map) return (Map<String,Object>)map; if ("course-generation".equals(task.operationType)) return Map.of("title","P0 course","lessons",List.of(Map.of("id","lesson-1","objective","掌握一个可练习的摄影目标")),"result","accepted","modelId",task.modelId); return Map.of("result", "accepted", "modelId", task.modelId); }
  private boolean valid(String operation, Map<String,Object> output) { if ("scene-analysis".equals(operation)) return sceneGraph.valid(output); if ("shooting-plan-generation".equals(operation)) return planGraph.valid(output); if ("photo-evaluation".equals(operation)) return evaluationGraph.valid(output); if ("reference-image-generation".equals(operation)) return referenceImageGraph.valid(output); return output != null && output.get("result") instanceof String value && !value.isBlank() && (!output.containsKey("lessons") || CourseGenerationValidator.valid(output)); }
  private AiTaskEntity owned(String token,String id) { var task=tasks.selectById(id); if(task==null || !task.accountId.equals(auth.requireAccountId(token))) throw new NotFound(); return task; }
  private Status status(AiTaskEntity task) { return new Status(task.id,State.valueOf(task.state), task.resultJson==null?null:read(task.resultJson),task.errorCode,new Trace(task.workflowVersion,task.modelId,task.promptVersion,task.ruleVersion,task.schemaVersion)); }
  private void publish(Status status) { for(var emitter: subscribers.getOrDefault(status.taskId,List.of())) emit(emitter,status); }
  private void emit(SseEmitter emitter, Status status) { try { emitter.send(SseEmitter.event().name("progress").data(status, MediaType.APPLICATION_JSON)); } catch(Exception e) { emitter.complete(); } }
  private String modelFor(String operation) { return "scene-analysis".equals(operation) || "photo-evaluation".equals(operation) ? sceneModel : "reference-image-generation".equals(operation) ? imageModel : "shooting-plan-generation".equals(operation) || "course-generation".equals(operation) ? "qwen3.7-plus" : "coach".equals(operation) || "course-feedback".equals(operation) ? coachModel : defaultModel; }
  private String write(Object value) { try{return json.writeValueAsString(value);}catch(Exception e){throw new IllegalArgumentException(e);} }
  private Map<String,Object> read(String value) { try{return json.readValue(value,new TypeReference<>(){});}catch(Exception e){throw new IllegalStateException(e);} }
  public static class CreateRequest { public String operationType; public Map<String,Object> input; }
  public record Created(String taskId, State state) {} public record Trace(String workflowVersion,String modelId,String promptVersion,String ruleVersion,String schemaVersion) {} public record Status(String taskId,State state,Map<String,Object> result,String errorCode,Trace trace) {}
  public static class BadRequest extends RuntimeException {} public static class NotFound extends RuntimeException {}
}
