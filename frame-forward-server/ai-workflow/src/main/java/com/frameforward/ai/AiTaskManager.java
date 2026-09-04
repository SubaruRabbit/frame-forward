package com.frameforward.ai;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/** 统一协调 AI 任务的持久化访问。 */
@Component
public class AiTaskManager {
    private final AiTaskMapper tasks;

    public AiTaskManager(AiTaskMapper tasks) {
        this.tasks = tasks;
    }

    public List<AiTaskEntity> findRunning() {
        return tasks.selectList(
                new LambdaQueryWrapper<AiTaskEntity>().eq(AiTaskEntity::getState, AiTaskRuntime.State.RUNNING.name()));
    }

    public AiTaskEntity findExisting(String accountId, String operationType, String idempotencyKey) {
        return tasks.selectOne(new LambdaQueryWrapper<AiTaskEntity>().eq(AiTaskEntity::getAccountId, accountId)
                .eq(AiTaskEntity::getOperationType, operationType).eq(AiTaskEntity::getIdempotencyKey, idempotencyKey));
    }

    public AiTaskEntity findById(String taskId) {
        return tasks.selectById(taskId);
    }

    public void create(AiTaskEntity task) {
        tasks.insert(task);
    }

    public void update(AiTaskEntity task) {
        task.updatedAt = Instant.now();
        tasks.updateById(task);
    }
}
