package com.frameforward.shooting.service;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.service.AiTaskRuntime;
import com.frameforward.auth.service.AuthService;
import com.frameforward.equipment.model.dto.UserEquipmentItem;
import com.frameforward.equipment.service.UserEquipmentService;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.entity.MediaEntity;
import com.frameforward.shooting.business.SceneAnalysisEquipmentNotOwned;
import com.frameforward.shooting.business.SceneAnalysisMediaNotOwned;
import com.frameforward.shooting.business.ShootingBusiness;
import com.frameforward.shooting.model.dto.SceneAnalysisRequest;

@Service
public class SceneAnalysisService {
    private final AuthService auth;
    private final MediaManager media;
    private final UserEquipmentService equipment;
    private final AiTaskRuntime tasks;
    private final ShootingBusiness business;

    public SceneAnalysisService(AuthService auth, MediaManager media, UserEquipmentService equipment,
            AiTaskRuntime tasks, ShootingBusiness business) {
        this.auth = auth;
        this.media = media;
        this.equipment = equipment;
        this.tasks = tasks;
        this.business = business;
    }

    @Transactional
    public AiTaskCreated create(String accessToken, String key, SceneAnalysisRequest request) {
        business.validate(request);
        String accountId = auth.requireAccountId(accessToken);
        MediaEntity ownedMedia = media.findOwnedEntity(accountId, request.environmentMediaId);
        if (ownedMedia == null) {
            throw new SceneAnalysisMediaNotOwned();
        }
        List<UserEquipmentItem> ownedEquipment = equipment.list(accountId).stream()
                .filter(item -> request.equipmentIds.contains(item.id())).toList();
        if (ownedEquipment.size() != request.equipmentIds.size()) {
            throw new SceneAnalysisEquipmentNotOwned();
        }
        AiTaskCreateRequest aiRequest = new AiTaskCreateRequest();
        aiRequest.operationType = "scene-analysis";
        aiRequest.input = business.sceneInput(ownedMedia, ownedEquipment, request);
        AiTaskCreated task = tasks.create(accessToken, key, aiRequest);
        business.persistSceneIfAbsent(accountId, ownedMedia, ownedEquipment, request, task.taskId());
        return task;
    }

}
