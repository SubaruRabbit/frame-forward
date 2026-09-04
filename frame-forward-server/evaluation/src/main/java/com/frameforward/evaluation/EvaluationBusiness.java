package com.frameforward.evaluation;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.frameforward.media.MediaEntity;

@Component
class EvaluationBusiness {
    private final EvaluationManager manager;
    EvaluationBusiness(EvaluationManager manager) {
        this.manager = manager;
    }
    void validate(PhotoEvaluationService.Request request) {
        if (request == null || request.mediaId == null || request.mediaId.isBlank())
            throw new PhotoEvaluationService.Invalid();
    }
    void requireOwnedSession(String accountId, String sessionId) {
        if (sessionId != null && !sessionId.isBlank() && !manager.hasOwnedSession(accountId, sessionId))
            throw new PhotoEvaluationService.NotFound();
    }
    PhotoEvaluationEntity cached(String accountId, MediaEntity photo, PhotoEvaluationService.Request request) {
        return manager.findCached(accountId, photo.contentHash, request.sessionId, request.reanalyze);
    }
    void save(String accountId, MediaEntity photo, PhotoEvaluationService.Request request, String taskId) {
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
    void saveResult(String taskId, String resultJson) {
        PhotoEvaluationEntity entry = manager.findByTaskId(taskId);
        if (entry != null && entry.resultJson == null) {
            entry.resultJson = resultJson;
            manager.update(entry);
        }
    }
}
