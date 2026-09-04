package com.frameforward.evaluation;
import java.time.Instant;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.media.MediaEntity;
import com.frameforward.media.MediaManager;
@Service
public class PhotoEvaluationService {
    private static final String RULE = "v1";
    private final AuthService auth;
    private final MediaManager media;
    private final PhotoEvaluationMapper evaluations;
    private final ShootingSessionMapper sessions;
    private final AiTaskRuntime tasks;
    private final ObjectMapper json;
    public PhotoEvaluationService(AuthService auth, MediaManager media, PhotoEvaluationMapper evaluations,
            ShootingSessionMapper sessions, AiTaskRuntime tasks, ObjectMapper json) {
        this.auth = auth;
        this.media = media;
        this.evaluations = evaluations;
        this.sessions = sessions;
        this.tasks = tasks;
        this.json = json;
    }
    @Transactional
    public AiTaskRuntime.Created create(String token, String key, Request request) {
        validate(request);
        String account = auth.requireAccountId(token);
        MediaEntity photo = findOwnedPhoto(account, request.mediaId);
        requireOwnedSession(account, request.sessionId);
        PhotoEvaluationEntity cached = findCachedEvaluation(account, photo, request);
        if (cached != null)
            return new AiTaskRuntime.Created(cached.aiTaskId, AiTaskRuntime.State.QUEUED);
        AiTaskRuntime.Created task = submitEvaluation(token, key, request, photo);
        evaluations.insert(newEvaluation(account, request, photo, task));
        return task;
    }
    private static void validate(Request request) {
        if (request == null || request.mediaId == null || request.mediaId.isBlank())
            throw new Invalid();
    }
    private MediaEntity findOwnedPhoto(String account, String mediaId) {
        MediaEntity photo = media.findOwned(account, mediaId);
        if (photo == null)
            throw new NotFound();
        return photo;
    }
    private void requireOwnedSession(String account, String sessionId) {
        if (sessionId != null && !sessionId.isBlank()
                && sessions.selectOne(
                        new LambdaQueryWrapper<ShootingSessionEntity>().eq(ShootingSessionEntity::getId, sessionId)
                                .eq(ShootingSessionEntity::getAccountId, account)) == null)
            throw new NotFound();
    }
    private PhotoEvaluationEntity findCachedEvaluation(String account, MediaEntity photo, Request request) {
        if (request.reanalyze)
            return null;
        var query = new LambdaQueryWrapper<PhotoEvaluationEntity>().eq(PhotoEvaluationEntity::getAccountId, account)
                .eq(PhotoEvaluationEntity::getContentHash, photo.contentHash)
                .eq(PhotoEvaluationEntity::getRuleVersion, RULE).eq(PhotoEvaluationEntity::getExecutionVersion, RULE);
        if (request.sessionId == null || request.sessionId.isBlank())
            query.isNull(PhotoEvaluationEntity::getSessionId);
        else
            query.eq(PhotoEvaluationEntity::getSessionId, request.sessionId);
        return evaluations.selectOne(query);
    }
    private AiTaskRuntime.Created submitEvaluation(String token, String key, Request request, MediaEntity photo) {
        var input = new LinkedHashMap<String, Object>();
        input.put("mediaId", photo.id);
        input.put("intent", request.intent == null ? "" : request.intent);
        input.put("hasExif", photo.exifJson != null && !photo.exifJson.equals("{}"));
        input.put("exif", photo.exifJson == null ? Map.of() : photo.exifJson);
        input.put("weights", Map.of("composition", .35, "light", .30, "subject", .20, "technical", .15));
        if (request.mockOutput != null)
            input.put("mockOutput", request.mockOutput);
        var ai = new AiTaskRuntime.CreateRequest();
        ai.operationType = "photo-evaluation";
        ai.input = input;
        return tasks.create(token, key, ai);
    }
    private PhotoEvaluationEntity newEvaluation(String account, Request request, MediaEntity photo,
            AiTaskRuntime.Created task) {
        PhotoEvaluationEntity entity = new PhotoEvaluationEntity();
        entity.id = UUID.randomUUID().toString();
        entity.accountId = account;
        entity.mediaId = photo.id;
        entity.sessionId = request.sessionId;
        entity.contentHash = photo.contentHash;
        entity.ruleVersion = RULE;
        entity.executionVersion = request.reanalyze ? RULE + "-r" + entity.id.substring(0, 8) : RULE;
        entity.aiTaskId = task.taskId();
        entity.createdAt = Instant.now();
        return entity;
    }
    @Transactional
    public AiTaskRuntime.Status result(String token, String taskId) {
        var status = tasks.get(token, taskId);
        if (status.result() != null) {
            var entry = evaluations.selectOne(
                    new LambdaQueryWrapper<PhotoEvaluationEntity>().eq(PhotoEvaluationEntity::getAiTaskId, taskId));
            if (entry != null && entry.resultJson == null)
                try {
                    entry.resultJson = json.writeValueAsString(status.result());
                    evaluations.updateById(entry);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
        }
        return status;
    }
    public static class Request {
        public String mediaId, intent, sessionId;
        public boolean reanalyze;
        public Map<String, Object> mockOutput;
    }
    public static class Invalid extends RuntimeException {
    }
    public static class NotFound extends RuntimeException {
    }
}
