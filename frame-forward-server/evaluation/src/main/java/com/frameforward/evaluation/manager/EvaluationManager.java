package com.frameforward.evaluation.manager;
import java.util.List;

import org.springframework.stereotype.Component;

import com.frameforward.evaluation.model.entity.*;
import com.frameforward.evaluation.repository.EvaluationRepository;
@Component
public class EvaluationManager {
    private final EvaluationRepository repository;
    public EvaluationManager(EvaluationRepository repository) {
        this.repository = repository;
    }
    public PhotoEvaluationEntity findCached(String accountId, String contentHash, String sessionId, boolean reanalyze) {
        return reanalyze ? null : repository.findCached(accountId, contentHash, sessionId);
    }
    public boolean hasOwnedSession(String accountId, String sessionId) {
        return repository.findOwnedSession(accountId, sessionId) != null;
    }
    public void save(PhotoEvaluationEntity evaluation) {
        repository.save(evaluation);
    }
    public PhotoEvaluationEntity findByTaskId(String taskId) {
        return repository.findByTaskId(taskId);
    }
    public void update(PhotoEvaluationEntity evaluation) {
        repository.update(evaluation);
    }
    public PhotoEvaluationEntity findOwnedEvaluation(String accountId, String evaluationId) {
        return repository.findOwnedEvaluation(accountId, evaluationId);
    }
    public ShootingSessionEntity findSession(String sessionId) {
        return repository.findSession(sessionId);
    }
    public RetakeLinkEntity findOwnedRetake(String accountId, String retakeId) {
        return repository.findOwnedRetake(accountId, retakeId);
    }
    public void saveRetakeIfAbsent(String accountId, PhotoEvaluationEntity original, PhotoEvaluationEntity retake) {
        if (repository.findRetakeById(retake.id) != null)
            return;
        RetakeLinkEntity link = new RetakeLinkEntity();
        link.retakeEvaluationId = retake.id;
        link.originalEvaluationId = original.id;
        link.accountId = accountId;
        link.sessionId = original.sessionId;
        link.createdAt = java.time.Instant.now();
        repository.saveRetake(link);
    }
    public PhotoEvaluationEntity latestOwned(String accountId, String mediaId) {
        return repository.latestOwned(accountId, mediaId);
    }
    public ShootingSessionEntity findOwnedSession(String accountId, String sessionId) {
        return repository.findOwnedSession(accountId, sessionId);
    }
    public RetakeLinkEntity relatedRetake(String accountId, String evaluationId) {
        return repository.relatedRetake(accountId, evaluationId);
    }
    public List<PhotoEvaluationEntity> completedCandidates(String accountId, String sessionId) {
        return repository.completedCandidates(accountId, sessionId);
    }
    public void deleteForWork(String accountId, String mediaId) {
        for (var evaluation : repository.listOwnedForWork(accountId, mediaId))
            repository.deleteRetakesForEvaluation(evaluation.id);
        repository.deleteEvaluationsForWork(accountId, mediaId);
    }
}
