package com.frameforward.ai.manager;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.frameforward.ai.gateway.AiTaskCompletionProcessor;
import com.frameforward.ai.model.dto.AiTaskCompletionContext;
import com.frameforward.ai.model.entity.AiTaskEntity;
import com.frameforward.ai.repository.AiTaskRepository;

/** 统一协调 AI 任务的持久化访问。 */
@Component
public class AiTaskManager {
    private final AiTaskRepository tasks;
    private final List<AiTaskCompletionProcessor> completionProcessors;

    public AiTaskManager(AiTaskRepository tasks, List<AiTaskCompletionProcessor> completionProcessors) {
        this.tasks = tasks;
        this.completionProcessors = completionProcessors;
    }

    public void completeResults(AiTaskEntity task, Map<String, Object> result) {
        var context = new AiTaskCompletionContext(task.id, task.accountId);
        completionProcessors.stream().filter(processor -> processor.supports(task.operationType))
                .forEach(processor -> processor.complete(context, result));
    }

    public List<AiTaskEntity> findRunning() {
        return tasks.findRunning();
    }

    public AiTaskEntity findExisting(String accountId, String operationType, String idempotencyKey) {
        return tasks.findExisting(accountId, operationType, idempotencyKey);
    }

    public AiTaskEntity findById(String taskId) {
        return tasks.findById(taskId);
    }

    public void create(AiTaskEntity task) {
        tasks.create(task);
    }

    public void update(AiTaskEntity task) {
        task.updatedAt = Instant.now();
        tasks.update(task);
    }
}
