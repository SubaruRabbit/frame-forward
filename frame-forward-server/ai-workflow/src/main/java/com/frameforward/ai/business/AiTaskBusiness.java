package com.frameforward.ai.business;
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
import com.frameforward.ai.manager.AiTaskManager;
import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskCreation;
import com.frameforward.ai.model.dto.AiTaskState;
import com.frameforward.ai.model.dto.AiTaskStatus;
import com.frameforward.ai.model.dto.AiTaskTrace;
import com.frameforward.ai.model.entity.AiTaskEntity;

/** 承载 AI 任务的创建、路由、重试与状态流转规则。 */
@Component
public class AiTaskBusiness {
    private final AiTaskManager tasks;
    private final ObjectMapper json;
    private final SceneAnalysisGraph sceneGraph;
    private final PlanGenerationGraph planGraph;
    private final PhotoEvaluationGraph evaluationGraph;
    private final ReferenceImageGraph referenceImageGraph;
    private final String defaultModel;
    private final String coachModel;
    private final String sceneModel;
    private final String imageModel;

    public AiTaskBusiness(AiTaskManager tasks, ObjectMapper json,
            @Value("${frame-forward.ai.qwen-routes.default:qwen-plus}") String defaultModel,
            @Value("${frame-forward.ai.qwen-routes.coach:qwen-plus}") String coachModel,
            @Value("${frame-forward.ai.qwen-routes.scene:qwen3.8-max}") String sceneModel,
            @Value("${frame-forward.ai.qwen-routes.image:qwen-image-3.0-pro}") String imageModel,
            SceneAnalysisGraph sceneGraph, PlanGenerationGraph planGraph, PhotoEvaluationGraph evaluationGraph,
            ReferenceImageGraph referenceImageGraph) {
        this.tasks = tasks;
        this.json = json;
        this.defaultModel = defaultModel;
        this.coachModel = coachModel;
        this.sceneModel = sceneModel;
        this.imageModel = imageModel;
        this.sceneGraph = sceneGraph;
        this.planGraph = planGraph;
        this.evaluationGraph = evaluationGraph;
        this.referenceImageGraph = referenceImageGraph;
    }

    public void recoverInterruptedTasks() {
        for (AiTaskEntity task : tasks.findRunning()) {
            task.state = AiTaskState.QUEUED.name();
            tasks.update(task);
        }
    }

    public AiTaskCreation create(String accountId, String key, AiTaskCreateRequest request) {
        AiTaskEntity existing = tasks.findExisting(accountId, request.operationType, key);
        if (existing != null)
            return new AiTaskCreation(new AiTaskCreated(existing.id, AiTaskState.valueOf(existing.state)), false);
        AiTaskEntity task = newTask(accountId, key, request);
        try {
            tasks.create(task);
        } catch (DuplicateKeyException duplicate) {
            AiTaskEntity found = tasks.findExisting(accountId, request.operationType, key);
            return new AiTaskCreation(new AiTaskCreated(found.id, AiTaskState.valueOf(found.state)), false);
        }
        return new AiTaskCreation(new AiTaskCreated(task.id, AiTaskState.QUEUED), true);
    }

    public AiTaskEntity findTask(String taskId) {
        return tasks.findById(taskId);
    }

    public AiTaskStatus statusForOwner(AiTaskEntity task, String accountId) {
        if (!task.accountId.equals(accountId))
            throw new AiTaskNotFound();
        return status(task);
    }

    public void run(String taskId, Consumer<AiTaskStatus> statusPublisher) {
        AiTaskEntity task = tasks.findById(taskId);
        if (task == null || !transition(task, AiTaskState.QUEUED, AiTaskState.RUNNING, statusPublisher))
            return;
        try {
            for (int attempt = 0; attempt < 3; attempt++) {
                Map<String, Object> output = outputFor(task);
                if (valid(task.operationType, output)) {
                    tasks.completeResults(task, output);
                    task.resultJson = write(output);
                    task.errorCode = null;
                    transition(task, AiTaskState.RUNNING, AiTaskState.SUCCEEDED, statusPublisher);
                    return;
                }
            }
            task.resultJson = null;
            task.errorCode = "INVALID_MODEL_OUTPUT";
            transition(task, AiTaskState.RUNNING, AiTaskState.FAILED, statusPublisher);
        } catch (RuntimeException exception) {
            task.resultJson = null;
            task.errorCode = "RUNTIME_ERROR";
            transition(task, AiTaskState.RUNNING, AiTaskState.FAILED, statusPublisher);
        }
    }

    public static boolean legal(AiTaskState from, AiTaskState to) {
        return (from == AiTaskState.QUEUED && (to == AiTaskState.RUNNING || to == AiTaskState.CANCELLED))
                || (from == AiTaskState.RUNNING
                        && (to == AiTaskState.SUCCEEDED || to == AiTaskState.FAILED || to == AiTaskState.CANCELLED));
    }

    private boolean transition(AiTaskEntity task, AiTaskState from, AiTaskState to,
            Consumer<AiTaskStatus> statusPublisher) {
        if (AiTaskState.valueOf(task.state) != from || !legal(from, to))
            return false;
        task.state = to.name();
        tasks.update(task);
        statusPublisher.accept(status(task));
        return true;
    }

    public static void validateCreateRequest(String key, AiTaskCreateRequest request) {
        if (key == null || key.isBlank() || key.length() > 128 || request == null || request.operationType == null
                || request.operationType.isBlank())
            throw new AiTaskBadRequest();
    }

    private AiTaskEntity newTask(String accountId, String key, AiTaskCreateRequest request) {
        AiTaskEntity task = new AiTaskEntity();
        task.id = UUID.randomUUID().toString();
        task.accountId = accountId;
        task.operationType = request.operationType;
        task.idempotencyKey = key;
        task.state = AiTaskState.QUEUED.name();
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

    private AiTaskStatus status(AiTaskEntity task) {
        return new AiTaskStatus(task.id, AiTaskState.valueOf(task.state),
                task.resultJson == null ? null : read(task.resultJson), task.errorCode, new AiTaskTrace(
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

}
