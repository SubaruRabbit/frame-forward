package com.frameforward.evaluation;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/** 仅清理绑定到指定作品的评测及其重拍关联，不触碰其他作品的评测结果。 */
@Component
@Primary
public class EvaluationWorkCleanup implements WorkEvaluationCleanup {
    private final PhotoEvaluationMapper evaluations;
    private final RetakeLinkMapper retakes;

    public EvaluationWorkCleanup(PhotoEvaluationMapper evaluations, RetakeLinkMapper retakes) {
        this.evaluations = evaluations;
        this.retakes = retakes;
    }

    @Override
    public void deleteForWork(String accountId, String mediaId) {
        var owned = evaluations.selectList(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                .eq(PhotoEvaluationEntity::getAccountId, accountId).eq(PhotoEvaluationEntity::getMediaId, mediaId));
        for (PhotoEvaluationEntity evaluation : owned) {
            retakes.delete(new LambdaQueryWrapper<RetakeLinkEntity>()
                    .eq(RetakeLinkEntity::getOriginalEvaluationId, evaluation.id).or()
                    .eq(RetakeLinkEntity::getRetakeEvaluationId, evaluation.id));
        }
        evaluations.delete(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                .eq(PhotoEvaluationEntity::getAccountId, accountId).eq(PhotoEvaluationEntity::getMediaId, mediaId));
    }
}
