package com.frameforward.ai;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.auth.AuthService;

/** 承载 AI 任务的创建、路由、重试与状态流转规则。 */
@Component
public class AiTaskBusiness {
    private final AiTaskManager tasks;
    private final ObjectMapper json;
    private final AuthService auth;
    private final SceneAnalysisGraph sceneGraph;
    private final PlanGenerationGraph planGraph;
    private final PhotoEvaluationGraph evaluationGraph;
    private final ReferenceImageGraph referenceImageGraph;
    private final List<AiTaskCompletionProcessor> completionProcessors;
    private final String defaultModel;
    private final String coachModel;
    private final String sceneModel;
    private final String imageModel;

    public AiTaskBusiness(AiTaskManager tasks, ObjectMapper json, AuthService auth,
            @Value("${frame-forward.ai.qwen-routes.default:qwen-plus}") String defaultModel,
            @Value("${frame-forward.ai.qwen-routes.coach:qwen-plus}") String coachModel,
            @Value("${frame-forward.ai.qwen-routes.scene:qwen3.8-max}") String sceneModel,
            @Value("${frame-forward.ai.qwen-routes.image:qwen-image-3.0-pro}") String imageModel,
            SceneAnalysisGraph sceneGraph, PlanGenerationGraph planGraph, PhotoEvaluationGraph evaluationGraph,
            ReferenceImageGraph referenceImageGraph, List<AiTaskCompletionProcessor> completionProcessors) {
        this.tasks = tasks;
        this.json = json;
        this.auth = auth;
        this.defaultModel = defaultModel;
        this.coachModel = coachModel;
        this.sceneModel = sceneModel;
        this.imageModel = imageModel;
        this.sceneGraph = sceneGraph;
        this.planGraph = planGraph;
        this.evaluationGraph = evaluationGraph;
        this.referenceImageGraph = referenceImageGraph;
        this.completionProcessors = completionProcessors;
    }

    public void recoverInterruptedTasks() {
        for (AiTaskEntity task : tasks.findRunning()) {
            task.state = AiTaskRuntime.State.QUEUED.name();
            tasks.update(task);
        }
    }

    public Creation create(String accessToken, String key, AiTaskRuntime.CreateRequest request) {
        validateCreateRequest(key, request);
        String accountId = auth.requireAccountId(accessToken);
        AiTaskEntity existing = tasks.findExisting(accountId, request.operationType, key);
        if (existing != null)
            return new Creation(new AiTaskRuntime.Created(existing.id, AiTaskRuntime.State.valueOf(existing.state)),
                    false);
        AiTaskEntity task = newTask(accountId, key, request);
        try {
            tasks.create(task);
        } catch (DuplicateKeyException duplicate) {
            AiTaskEntity found = tasks.findExisting(accountId, request.operationType, key);
            return new Creation(new AiTaskRuntime.Created(found.id, AiTaskRuntime.State.valueOf(found.state)), false);
        }
        return new Creation(new AiTaskRuntime.Created(task.id, AiTaskRuntime.State.QUEUED), true);
    }

    public AiTaskRuntime.Status get(String accessToken, String taskId) {
        AiTaskEntity task = tasks.findById(taskId);
        if (task == null || !task.accountId.equals(auth.requireAccountId(accessToken)))
            throw new AiTaskRuntime.NotFound();
        return status(task);
    }

    public void run(String taskId, Consumer<AiTaskRuntime.Status> statusPublisher) {
        AiTaskEntity task = tasks.findById(taskId);
        if (task == null || !transition(task, AiTaskRuntime.State.QUEUED, AiTaskRuntime.State.RUNNING, statusPublisher))
            return;
        try {
            for (int attempt = 0; attempt < 3; attempt++) {
                Map<String, Object> output = outputFor(task);
                if (valid(task.operationType, output)) {
                    completionProcessors.stream().filter(processor -> processor.supports(task.operationType))
                            .forEach(processor -> processor.complete(task, output));
                    task.resultJson = write(output);
                    task.errorCode = null;
                    transition(task, AiTaskRuntime.State.RUNNING, AiTaskRuntime.State.SUCCEEDED, statusPublisher);
                    return;
                }
            }
            task.resultJson = null;
            task.errorCode = "INVALID_MODEL_OUTPUT";
            transition(task, AiTaskRuntime.State.RUNNING, AiTaskRuntime.State.FAILED, statusPublisher);
        } catch (RuntimeException exception) {
            task.resultJson = null;
            task.errorCode = "RUNTIME_ERROR";
            transition(task, AiTaskRuntime.State.RUNNING, AiTaskRuntime.State.FAILED, statusPublisher);
        }
    }

    static boolean legal(AiTaskRuntime.State from, AiTaskRuntime.State to) {
        return (from == AiTaskRuntime.State.QUEUED
                && (to == AiTaskRuntime.State.RUNNING || to == AiTaskRuntime.State.CANCELLED))
                || (from == AiTaskRuntime.State.RUNNING && (to == AiTaskRuntime.State.SUCCEEDED
                        || to == AiTaskRuntime.State.FAILED || to == AiTaskRuntime.State.CANCELLED));
    }

    private boolean transition(AiTaskEntity task, AiTaskRuntime.State from, AiTaskRuntime.State to,
            Consumer<AiTaskRuntime.Status> statusPublisher) {
        if (AiTaskRuntime.State.valueOf(task.state) != from || !legal(from, to))
            return false;
        task.state = to.name();
        tasks.update(task);
        statusPublisher.accept(status(task));
        return true;
    }

    private static void validateCreateRequest(String key, AiTaskRuntime.CreateRequest request) {
        if (key == null || key.isBlank() || key.length() > 128 || request == null || request.operationType == null
                || request.operationType.isBlank())
            throw new AiTaskRuntime.BadRequest();
    }

    private AiTaskEntity newTask(String accountId, String key, AiTaskRuntime.CreateRequest request) {
        AiTaskEntity task = new AiTaskEntity();
        task.id = UUID.randomUUID().toString();
        task.accountId = accountId;
        task.operationType = request.operationType;
        task.idempotencyKey = key;
        task.state = AiTaskRuntime.State.QUEUED.name();
        task.workflowVersion = "v1";
        task.modelId = modelFor(request.operationType);
        task.promptVersion = "v1";
        task.ruleVersion = "v1";
        task.schemaVersion = "v1";
        task.inputJson = write(request.input == null ? Map.of() : request.input);
        task.createdAt = task.updatedAt = Instant.now();
        return task;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> outputFor(AiTaskEntity task) {
        Map<String, Object> input = read(task.inputJson);
        if ("scene-analysis".equals(task.operationType))
            return sceneGraph.execute(input);
        if ("shooting-plan-generation".equals(task.operationType))
            return planGraph.execute(input);
        if ("photo-evaluation".equals(task.operationType))
            return evaluationGraph.execute(input);
        if ("reference-image-generation".equals(task.operationType))
            return referenceImageGraph.execute(input);
        Object supplied = input.get("mockOutput");
        if (supplied instanceof Map<?, ?> map)
            return (Map<String, Object>) map;
        if ("course-generation".equals(task.operationType))
            return Map.of("title", "P0 course", "lessons",
                    List.of(Map.of("id", "lesson-1", "objective", "掌握一个可练习的摄影目标")), "result", "accepted", "modelId",
                    task.modelId);
        return Map.of("result", "accepted", "modelId", task.modelId);
    }

    private boolean valid(String operation, Map<String, Object> output) {
        if ("scene-analysis".equals(operation))
            return sceneGraph.valid(output);
        if ("shooting-plan-generation".equals(operation))
            return planGraph.valid(output);
        if ("photo-evaluation".equals(operation))
            return evaluationGraph.valid(output);
        if ("reference-image-generation".equals(operation))
            return referenceImageGraph.valid(output);
        return output != null && output.get("result") instanceof String value && !value.isBlank()
                && (!output.containsKey("lessons") || CourseGenerationValidator.valid(output));
    }

    private AiTaskRuntime.Status status(AiTaskEntity task) {
        return new AiTaskRuntime.Status(task.id, AiTaskRuntime.State.valueOf(task.state),
                task.resultJson == null ? null : read(task.resultJson), task.errorCode, new AiTaskRuntime.Trace(
                        task.workflowVersion, task.modelId, task.promptVersion, task.ruleVersion, task.schemaVersion));
    }

    private String modelFor(String operation) {
        return "scene-analysis".equals(operation) || "photo-evaluation".equals(operation)
                ? sceneModel
                : "reference-image-generation".equals(operation)
                        ? imageModel
                        : "shooting-plan-generation".equals(operation) || "course-generation".equals(operation)
                                ? "qwen3.7-plus"
                                : "coach".equals(operation) || "course-feedback".equals(operation)
                                        ? coachModel
                                        : defaultModel;
    }

    private String write(Object value) {
        try {
            return json.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    private Map<String, Object> read(String value) {
        try {
            return json.readValue(value, new TypeReference<>() {
            });
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    public record Creation(AiTaskRuntime.Created created, boolean schedule) {
    }
}
