package com.frameforward.ai.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.ai.mapper.AiTaskMapper;
import com.frameforward.ai.model.entity.AiTaskEntity;

/** 封装 AI 任务的数据库访问，查询条件与持久化状态值保持一致。 */
@Repository
public class AiTaskRepository {
    private final AiTaskMapper tasks;
    public AiTaskRepository(AiTaskMapper tasks) {
        this.tasks = tasks;
    }
    public List<AiTaskEntity> findRunning() {
        return tasks.selectList(new LambdaQueryWrapper<AiTaskEntity>().eq(AiTaskEntity::getState, "RUNNING"));
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
        tasks.updateById(task);
    }
}
