package com.frameforward.evaluation.gateway;

/** 删除作品时由作品集模块调用的评测清理端口。 */
public interface WorkEvaluationCleanup {
    void deleteForWork(String accountId, String mediaId);
}
