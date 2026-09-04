package com.frameforward.evaluation;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.media.MediaEntity;
import com.frameforward.media.MediaManager;
@Service
public class PhotoEvaluationService {
    private final AuthService auth;
    private final MediaManager media;
    private final AiTaskRuntime tasks;
    private final ObjectMapper json;
    private final EvaluationBusiness business;
    public PhotoEvaluationService(AuthService auth, MediaManager media, AiTaskRuntime tasks, ObjectMapper json,
            EvaluationBusiness business) {
        this.auth = auth;
        this.media = media;
        this.tasks = tasks;
        this.json = json;
        this.business = business;
    }
    @Transactional
    public AiTaskRuntime.Created create(String token, String key, Request request) {
        business.validate(request);
        String account = auth.requireAccountId(token);
        MediaEntity photo = findOwnedPhoto(account, request.mediaId);
        business.requireOwnedSession(account, request.sessionId);
        PhotoEvaluationEntity cached = business.cached(account, photo, request);
        if (cached != null)
            return new AiTaskRuntime.Created(cached.aiTaskId, AiTaskRuntime.State.QUEUED);
        AiTaskRuntime.Created task = submitEvaluation(token, key, request, photo);
        business.save(account, photo, request, task.taskId());
        return task;
    }
    private MediaEntity findOwnedPhoto(String account, String mediaId) {
        MediaEntity photo = media.findOwned(account, mediaId);
        if (photo == null)
            throw new NotFound();
        return photo;
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
    @Transactional
    public AiTaskRuntime.Status result(String token, String taskId) {
        var status = tasks.get(token, taskId);
        if (status.result() != null) {
            try {
                business.saveResult(taskId, json.writeValueAsString(status.result()));
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
