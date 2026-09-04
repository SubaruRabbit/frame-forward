package com.frameforward.shooting;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;

@Service
public class ShootingPlanService {
    private final AuthService auth;
    private final AiTaskRuntime tasks;
    private final ShootingBusiness business;

    public ShootingPlanService(AuthService auth, AiTaskRuntime tasks, ShootingBusiness business) {
        this.auth = auth;
        this.tasks = tasks;
        this.business = business;
    }

    @Transactional
    public AiTaskRuntime.Created create(String token, String key, Request request) {
        business.validate(request);
        String account = auth.requireAccountId(token);
        SceneAnalysisEntity scene = business.findOwnedScene(account, request.sceneAnalysisId);
        AiTaskRuntime.CreateRequest taskRequest = new AiTaskRuntime.CreateRequest();
        taskRequest.operationType = "shooting-plan-generation";
        taskRequest.input = business.planInput(scene, request);
        AiTaskRuntime.Created task = tasks.create(token, key, taskRequest);
        business.persistPlanIfAbsent(account, scene, task.taskId());
        return task;
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
