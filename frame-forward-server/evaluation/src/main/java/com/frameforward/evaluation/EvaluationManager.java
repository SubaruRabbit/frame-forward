package com.frameforward.evaluation;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Component
class EvaluationManager {
    private final PhotoEvaluationMapper evaluations;
    private final ShootingSessionMapper sessions;
    private final RetakeLinkMapper links;

    EvaluationManager(PhotoEvaluationMapper evaluations, ShootingSessionMapper sessions, RetakeLinkMapper links) {
        this.evaluations = evaluations;
        this.sessions = sessions;
        this.links = links;
    }

    PhotoEvaluationEntity findCached(String accountId, String contentHash, String sessionId, boolean reanalyze) {
        if (reanalyze)
            return null;
        var query = new LambdaQueryWrapper<PhotoEvaluationEntity>().eq(PhotoEvaluationEntity::getAccountId, accountId)
                .eq(PhotoEvaluationEntity::getContentHash, contentHash).eq(PhotoEvaluationEntity::getRuleVersion, "v1")
                .eq(PhotoEvaluationEntity::getExecutionVersion, "v1");
        if (sessionId == null || sessionId.isBlank())
            query.isNull(PhotoEvaluationEntity::getSessionId);
        else
            query.eq(PhotoEvaluationEntity::getSessionId, sessionId);
        return evaluations.selectOne(query);
    }

    boolean hasOwnedSession(String accountId, String sessionId) {
        return sessions
                .selectOne(new LambdaQueryWrapper<ShootingSessionEntity>().eq(ShootingSessionEntity::getId, sessionId)
                        .eq(ShootingSessionEntity::getAccountId, accountId)) != null;
    }

    void save(PhotoEvaluationEntity evaluation) {
        evaluations.insert(evaluation);
    }

    PhotoEvaluationEntity findByTaskId(String taskId) {
        return evaluations.selectOne(
                new LambdaQueryWrapper<PhotoEvaluationEntity>().eq(PhotoEvaluationEntity::getAiTaskId, taskId));
    }

    void update(PhotoEvaluationEntity evaluation) {
        evaluations.updateById(evaluation);
    }

    PhotoEvaluationEntity findOwnedEvaluation(String accountId, String evaluationId) {
        return evaluations.selectOne(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                .eq(PhotoEvaluationEntity::getId, evaluationId).eq(PhotoEvaluationEntity::getAccountId, accountId));
    }

    ShootingSessionEntity findSession(String sessionId) {
        return sessions.selectById(sessionId);
    }

    RetakeLinkEntity findOwnedRetake(String accountId, String retakeId) {
        return links.selectOne(new LambdaQueryWrapper<RetakeLinkEntity>()
                .eq(RetakeLinkEntity::getRetakeEvaluationId, retakeId).eq(RetakeLinkEntity::getAccountId, accountId));
    }

    void saveRetakeIfAbsent(String accountId, PhotoEvaluationEntity original, PhotoEvaluationEntity retake) {
        if (links.selectById(retake.id) != null)
            return;
        RetakeLinkEntity link = new RetakeLinkEntity();
        link.retakeEvaluationId = retake.id;
        link.originalEvaluationId = original.id;
        link.accountId = accountId;
        link.sessionId = original.sessionId;
        link.createdAt = java.time.Instant.now();
        links.insert(link);
    }
}
