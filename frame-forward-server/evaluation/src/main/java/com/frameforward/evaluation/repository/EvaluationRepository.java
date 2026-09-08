package com.frameforward.evaluation.repository;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.evaluation.mapper.PhotoEvaluationMapper;
import com.frameforward.evaluation.mapper.RetakeLinkMapper;
import com.frameforward.evaluation.mapper.ShootingSessionMapper;
import com.frameforward.evaluation.model.entity.PhotoEvaluationEntity;
import com.frameforward.evaluation.model.entity.RetakeLinkEntity;
import com.frameforward.evaluation.model.entity.ShootingSessionEntity;

@Repository
public class EvaluationRepository {
    public PhotoEvaluationEntity latestOwned(String accountId, String mediaId) {
        return evaluations.selectOne(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                .eq(PhotoEvaluationEntity::getAccountId, accountId).eq(PhotoEvaluationEntity::getMediaId, mediaId)
                .orderByDesc(PhotoEvaluationEntity::getCreatedAt).last("LIMIT 1"));
    }
    public RetakeLinkEntity relatedRetake(String accountId, String evaluationId) {
        return links.selectOne(new LambdaQueryWrapper<RetakeLinkEntity>().eq(RetakeLinkEntity::getAccountId, accountId)
                .and(query -> query.eq(RetakeLinkEntity::getRetakeEvaluationId, evaluationId).or()
                        .eq(RetakeLinkEntity::getOriginalEvaluationId, evaluationId)));
    }
    public java.util.List<PhotoEvaluationEntity> completedCandidates(String accountId, String sessionId) {
        return evaluations.selectList(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                .eq(PhotoEvaluationEntity::getAccountId, accountId).eq(PhotoEvaluationEntity::getSessionId, sessionId)
                .isNotNull(PhotoEvaluationEntity::getResultJson));
    }
    public java.util.List<PhotoEvaluationEntity> listOwnedForWork(String accountId, String mediaId) {
        return evaluations.selectList(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                .eq(PhotoEvaluationEntity::getAccountId, accountId).eq(PhotoEvaluationEntity::getMediaId, mediaId));
    }
    public void deleteRetakesForEvaluation(String evaluationId) {
        links.delete(
                new LambdaQueryWrapper<RetakeLinkEntity>().eq(RetakeLinkEntity::getOriginalEvaluationId, evaluationId)
                        .or().eq(RetakeLinkEntity::getRetakeEvaluationId, evaluationId));
    }
    public void deleteEvaluationsForWork(String accountId, String mediaId) {
        evaluations.delete(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                .eq(PhotoEvaluationEntity::getAccountId, accountId).eq(PhotoEvaluationEntity::getMediaId, mediaId));
    }
    private final PhotoEvaluationMapper evaluations;
    private final ShootingSessionMapper sessions;
    private final RetakeLinkMapper links;

    public EvaluationRepository(PhotoEvaluationMapper evaluations, ShootingSessionMapper sessions,
            RetakeLinkMapper links) {
        this.evaluations = evaluations;
        this.sessions = sessions;
        this.links = links;
    }

    public PhotoEvaluationEntity findCached(String accountId, String contentHash, String sessionId) {
        var query = new LambdaQueryWrapper<PhotoEvaluationEntity>().eq(PhotoEvaluationEntity::getAccountId, accountId)
                .eq(PhotoEvaluationEntity::getContentHash, contentHash).eq(PhotoEvaluationEntity::getRuleVersion, "v1")
                .eq(PhotoEvaluationEntity::getExecutionVersion, "v1");
        if (sessionId == null || sessionId.isBlank())
            query.isNull(PhotoEvaluationEntity::getSessionId);
        else
            query.eq(PhotoEvaluationEntity::getSessionId, sessionId);
        return evaluations.selectOne(query);
    }

    public ShootingSessionEntity findOwnedSession(String accountId, String sessionId) {
        return sessions.selectOne(new LambdaQueryWrapper<ShootingSessionEntity>()
                .eq(ShootingSessionEntity::getId, sessionId).eq(ShootingSessionEntity::getAccountId, accountId));
    }

    public void save(PhotoEvaluationEntity evaluation) {
        evaluations.insert(evaluation);
    }

    public PhotoEvaluationEntity findByTaskId(String taskId) {
        return evaluations.selectOne(
                new LambdaQueryWrapper<PhotoEvaluationEntity>().eq(PhotoEvaluationEntity::getAiTaskId, taskId));
    }

    public void update(PhotoEvaluationEntity evaluation) {
        evaluations.updateById(evaluation);
    }

    public PhotoEvaluationEntity findOwnedEvaluation(String accountId, String evaluationId) {
        return evaluations.selectOne(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                .eq(PhotoEvaluationEntity::getId, evaluationId).eq(PhotoEvaluationEntity::getAccountId, accountId));
    }

    public ShootingSessionEntity findSession(String sessionId) {
        return sessions.selectById(sessionId);
    }

    public RetakeLinkEntity findOwnedRetake(String accountId, String retakeId) {
        return links.selectOne(new LambdaQueryWrapper<RetakeLinkEntity>()
                .eq(RetakeLinkEntity::getRetakeEvaluationId, retakeId).eq(RetakeLinkEntity::getAccountId, accountId));
    }

    public RetakeLinkEntity findRetakeById(String id) {
        return links.selectById(id);
    }
    public void saveRetake(RetakeLinkEntity link) {
        links.insert(link);
    }
}
