package com.frameforward.evaluation;
import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
@TableName("photo_evaluations")
public class PhotoEvaluationEntity {
    @TableId
    public String id;
    public String accountId, mediaId, contentHash, ruleVersion, executionVersion, aiTaskId, resultJson, sessionId;
    public Instant createdAt;
    public String getId() {
        return id;
    }
    public String getAccountId() {
        return accountId;
    }
    public String getMediaId() {
        return mediaId;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public String getContentHash() {
        return contentHash;
    }
    public String getRuleVersion() {
        return ruleVersion;
    }
    public String getExecutionVersion() {
        return executionVersion;
    }
    public String getAiTaskId() {
        return aiTaskId;
    }
    public String getSessionId() {
        return sessionId;
    }
    public String getResultJson() {
        return resultJson;
    }
}
