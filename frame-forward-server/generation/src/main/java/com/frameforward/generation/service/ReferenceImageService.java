package com.frameforward.generation.service;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.service.AiTaskRuntime;
import com.frameforward.auth.service.AuthService;
import com.frameforward.generation.business.OwnershipMissing;
import com.frameforward.generation.business.ReferenceImageBusiness;
import com.frameforward.generation.model.dto.ReferenceImageRequest;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.entity.MediaEntity;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;

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
    public AiTaskCreated create(String token, String key, ReferenceImageRequest request) {
        business.validate(request);
        String account = auth.requireAccountId(token);
        MediaEntity scene = media.findOwnedEntity(account, request.environmentMediaId);
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
        AiTaskCreateRequest aiRequest = new AiTaskCreateRequest();
        aiRequest.operationType = "reference-image-generation";
        aiRequest.input = input;
        AiTaskCreated task = tasks.create(token, key, aiRequest);
        business.persistReference(account, scene.id, plan.id, task.taskId(), request.selectedPlan, prompt);
        return task;
    }

}
