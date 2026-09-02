package com.frameforward.shooting;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
@Service
public class ShootingPlanService {
    private final AuthService auth;
    private final SceneAnalysisMapper scenes;
    private final ShootingPlanMapper plans;
    private final AiTaskRuntime tasks;
    private final ObjectMapper json;
    public ShootingPlanService(AuthService auth, SceneAnalysisMapper scenes, ShootingPlanMapper plans,
            AiTaskRuntime tasks, ObjectMapper json) {
        this.auth = auth;
        this.scenes = scenes;
        this.plans = plans;
        this.tasks = tasks;
        this.json = json;
    }
    @Transactional
    public AiTaskRuntime.Created create(String token, String key, Request request) {
        validate(request);
        String account = auth.requireAccountId(token);
        var scene = findOwnedScene(account, request.sceneAnalysisId);
        var task = submitPlanGeneration(token, key, scene, request);
        persistPlanIfAbsent(account, scene, task);
        return task;
    }
    private static void validate(Request request) {
        if (request == null || request.sceneAnalysisId == null || request.sceneAnalysisId.isBlank())
            throw new InvalidRequest();
    }
    private SceneAnalysisEntity findOwnedScene(String account, String sceneAnalysisId) {
        var scene = scenes.selectOne(new LambdaQueryWrapper<SceneAnalysisEntity>()
                .eq(SceneAnalysisEntity::getId, sceneAnalysisId).eq(SceneAnalysisEntity::getAccountId, account));
        if (scene == null)
            throw new SceneNotFound();
        return scene;
    }
    private AiTaskRuntime.Created submitPlanGeneration(String token, String key, SceneAnalysisEntity scene,
            Request request) {
        var input = new LinkedHashMap<String, Object>();
        input.put("scene", Map.of("id", scene.id, "subject", scene.subjectText, "style", scene.targetStyle));
        input.put("equipmentSnapshot", read(scene.equipmentSnapshotJson));
        if (request.mockOutput != null)
            input.put("mockOutput", request.mockOutput);
        var taskRequest = new AiTaskRuntime.CreateRequest();
        taskRequest.operationType = "shooting-plan-generation";
        taskRequest.input = input;
        return tasks.create(token, key, taskRequest);
    }
    private void persistPlanIfAbsent(String account, SceneAnalysisEntity scene, AiTaskRuntime.Created task) {
        if (plans.selectOne(new LambdaQueryWrapper<ShootingPlanEntity>().eq(ShootingPlanEntity::getAiTaskId,
                task.taskId())) == null) {
            plans.insert(newPlan(account, scene, task));
        }
    }
    private ShootingPlanEntity newPlan(String account, SceneAnalysisEntity scene, AiTaskRuntime.Created task) {
        var entity = new ShootingPlanEntity();
        entity.id = UUID.randomUUID().toString();
        entity.accountId = account;
        entity.sceneAnalysisId = scene.id;
        entity.aiTaskId = task.taskId();
        entity.sceneSnapshotJson = write(
                Map.of("id", scene.id, "subject", scene.subjectText, "style", scene.targetStyle));
        entity.equipmentSnapshotJson = scene.equipmentSnapshotJson;
        entity.createdAt = Instant.now();
        return entity;
    }
    private Object read(String value) {
        try {
            return json.readValue(value, Object.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    private String write(Object value) {
        try {
            return json.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    public static class Request {
        public String sceneAnalysisId;
        public Map<String, Object> mockOutput;
    }
    public static class InvalidRequest extends RuntimeException {
    }
    public static class SceneNotFound extends RuntimeException {
    }
}
