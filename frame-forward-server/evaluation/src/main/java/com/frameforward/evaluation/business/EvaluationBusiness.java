package com.frameforward.evaluation.business;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.frameforward.evaluation.manager.EvaluationManager;
import com.frameforward.evaluation.model.dto.PhotoEvaluationRequest;
import com.frameforward.evaluation.model.entity.PhotoEvaluationEntity;
import com.frameforward.media.model.entity.MediaEntity;

@Component
public class EvaluationBusiness {
    private final EvaluationManager manager;
    public EvaluationBusiness(EvaluationManager manager) {
        this.manager = manager;
    }
    public void validate(PhotoEvaluationRequest request) {
        if (request == null || request.mediaId == null || request.mediaId.isBlank())
            throw new PhotoEvaluationInvalid();
    }
    public void requireOwnedSession(String accountId, String sessionId) {
        if (sessionId != null && !sessionId.isBlank() && !manager.hasOwnedSession(accountId, sessionId))
            throw new PhotoEvaluationNotFound();
    }
    public PhotoEvaluationEntity cached(String accountId, MediaEntity photo, PhotoEvaluationRequest request) {
        return manager.findCached(accountId, photo.contentHash, request.sessionId, request.reanalyze);
    }
    public void save(String accountId, MediaEntity photo, PhotoEvaluationRequest request, String taskId) {
        PhotoEvaluationEntity value = new PhotoEvaluationEntity();
        value.id = UUID.randomUUID().toString();
        value.accountId = accountId;
        value.mediaId = photo.id;
        value.sessionId = request.sessionId;
        value.contentHash = photo.contentHash;
        value.ruleVersion = "v1";
        value.executionVersion = request.reanalyze ? "v1-r" + value.id.substring(0, 8) : "v1";
        value.aiTaskId = taskId;
        value.createdAt = Instant.now();
        manager.save(value);
    }
    public void saveResult(String taskId, String resultJson) {
        PhotoEvaluationEntity entry = manager.findByTaskId(taskId);
        if (entry != null && entry.resultJson == null) {
            entry.resultJson = resultJson;
            manager.update(entry);
        }
    }
}
