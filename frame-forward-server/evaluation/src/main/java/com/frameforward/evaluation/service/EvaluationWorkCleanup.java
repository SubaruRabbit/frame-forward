package com.frameforward.evaluation.service;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.frameforward.evaluation.gateway.WorkEvaluationCleanup;
import com.frameforward.evaluation.manager.EvaluationManager;
/** 仅清理绑定到指定作品的评测及其重拍关联，不触碰其他作品的评测结果。 */
@Component
@Primary
public class EvaluationWorkCleanup implements WorkEvaluationCleanup {
    private final EvaluationManager manager;
    public EvaluationWorkCleanup(EvaluationManager manager) {
        this.manager = manager;
    }
    @Override
    public void deleteForWork(String accountId, String mediaId) {
        manager.deleteForWork(accountId, mediaId);
    }
}
