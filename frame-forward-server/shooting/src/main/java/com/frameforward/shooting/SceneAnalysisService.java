package com.frameforward.shooting;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.equipment.UserEquipmentService;
import com.frameforward.media.MediaEntity;
import com.frameforward.media.MediaMapper;

@Service
public class SceneAnalysisService {
    private final AuthService auth;
    private final MediaMapper media;
    private final UserEquipmentService equipment;
    private final AiTaskRuntime tasks;
    private final SceneAnalysisMapper analyses;
    private final ObjectMapper json;

    public SceneAnalysisService(AuthService auth, MediaMapper media, UserEquipmentService equipment,
            AiTaskRuntime tasks, SceneAnalysisMapper analyses, ObjectMapper json) {
        this.auth = auth;
        this.media = media;
        this.equipment = equipment;
        this.tasks = tasks;
        this.analyses = analyses;
        this.json = json;
    }

    @Transactional
    public AiTaskRuntime.Created create(String accessToken, String key, Request request) {
        validate(request);
        String accountId = auth.requireAccountId(accessToken);
        MediaEntity ownedMedia = media.selectOne(new LambdaQueryWrapper<MediaEntity>()
                .eq(MediaEntity::getId, request.environmentMediaId).eq(MediaEntity::getOwnerId, accountId));
        if (ownedMedia == null)
            throw new MediaNotOwned();
        List<UserEquipmentService.Item> ownedEquipment = equipment.list(accountId).stream()
                .filter(item -> request.equipmentIds.contains(item.id())).toList();
        if (ownedEquipment.size() != request.equipmentIds.size())
            throw new EquipmentNotOwned();
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("environmentMediaId", ownedMedia.id);
        input.put("subjectType", request.subjectType);
        input.put("subject", request.subject);
        input.put("targetStyle", request.targetStyle);
        input.put("timeConstraintMinutes", request.timeConstraintMinutes);
        input.put("equipment", ownedEquipment);
        if (request.mockOutput != null)
            input.put("mockOutput", request.mockOutput);
        AiTaskRuntime.CreateRequest aiRequest = new AiTaskRuntime.CreateRequest();
        aiRequest.operationType = "scene-analysis";
        aiRequest.input = input;
        AiTaskRuntime.Created task = tasks.create(accessToken, key, aiRequest);
        if (analyses.selectOne(new LambdaQueryWrapper<SceneAnalysisEntity>().eq(SceneAnalysisEntity::getAiTaskId,
                task.taskId())) == null) {
            SceneAnalysisEntity analysis = new SceneAnalysisEntity();
            analysis.id = UUID.randomUUID().toString();
            analysis.accountId = accountId;
            analysis.environmentMediaId = ownedMedia.id;
            analysis.subjectType = request.subjectType;
            analysis.subjectText = request.subject;
            analysis.targetStyle = request.targetStyle;
            analysis.timeConstraintMinutes = request.timeConstraintMinutes;
            analysis.equipmentSnapshotJson = write(ownedEquipment);
            analysis.aiTaskId = task.taskId();
            analysis.createdAt = Instant.now();
            analyses.insert(analysis);
        }
        return task;
    }

    private void validate(Request request) {
        if (request == null || blank(request.environmentMediaId) || blank(request.subjectType) || blank(request.subject)
                || blank(request.targetStyle) || request.timeConstraintMinutes == null
                || request.timeConstraintMinutes < 1 || request.timeConstraintMinutes > 1440
                || request.equipmentIds == null)
            throw new InvalidRequest();
    }
    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
    private String write(Object value) {
        try {
            return json.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    public static class Request {
        public String environmentMediaId;
        public String subjectType;
        public String subject;
        public String targetStyle;
        public Integer timeConstraintMinutes;
        public List<String> equipmentIds;
        public Map<String, Object> mockOutput;
    }
    public static class InvalidRequest extends RuntimeException {
    }
    public static class MediaNotOwned extends RuntimeException {
    }
    public static class EquipmentNotOwned extends RuntimeException {
    }
}
