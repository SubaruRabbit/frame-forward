package com.frameforward.generation;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.media.MediaEntity;
import com.frameforward.media.MediaManager;
import com.frameforward.shooting.ShootingPlanEntity;

@Service
public class ReferenceImageService {
    private final AuthService auth;
    private final MediaManager media;
    private final AiTaskRuntime tasks;
    private final ReferenceImageBusiness business;
    public ReferenceImageService(AuthService auth, MediaManager media, AiTaskRuntime tasks,
            ReferenceImageBusiness business) {
        this.auth = auth;
        this.media = media;
        this.tasks = tasks;
        this.business = business;
    }
    @Transactional
    public AiTaskRuntime.Created create(String token, String key, Request request) {
        business.validate(request);
        String account = auth.requireAccountId(token);
        MediaEntity scene = media.findOwned(account, request.environmentMediaId);
        ShootingPlanEntity plan = business.findOwnedPlan(account, request.shootingPlanId);
        if (scene == null || plan == null)
            throw new OwnershipMissing();
        String prompt = business.controlledPrompt(request.selectedPlan);
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("environmentMediaId", scene.id);
        input.put("shootingPlanId", plan.id);
        input.put("selectedPlan", request.selectedPlan);
        input.put("prompt", prompt);
        if (request.mockOutput != null)
            input.put("mockOutput", request.mockOutput);
        AiTaskRuntime.CreateRequest aiRequest = new AiTaskRuntime.CreateRequest();
        aiRequest.operationType = "reference-image-generation";
        aiRequest.input = input;
        AiTaskRuntime.Created task = tasks.create(token, key, aiRequest);
        business.persistReference(account, scene.id, plan.id, task.taskId(), request.selectedPlan, prompt);
        return task;
    }
    public static class Request {
        public String environmentMediaId, shootingPlanId;
        public Map<String, Object> selectedPlan, mockOutput;
    }
    public static class InvalidRequest extends RuntimeException {
    }
    public static class OwnershipMissing extends RuntimeException {
    }
}
