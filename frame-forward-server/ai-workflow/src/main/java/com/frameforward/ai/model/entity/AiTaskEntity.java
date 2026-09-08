package com.frameforward.ai.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("ai_tasks")
public class AiTaskEntity {
    @TableId
    public String id;
    public String accountId, operationType, idempotencyKey, state, workflowVersion, modelId, promptVersion, ruleVersion,
            schemaVersion, inputJson, resultJson, errorCode;
    public Instant createdAt, updatedAt;
    public AiTaskEntity() {
    }
    public String getId() {
        return id;
    }
    public String getAccountId() {
        return accountId;
    }
    public String getOperationType() {
        return operationType;
    }
    public String getIdempotencyKey() {
        return idempotencyKey;
    }
    public String getState() {
        return state;
    }
}
