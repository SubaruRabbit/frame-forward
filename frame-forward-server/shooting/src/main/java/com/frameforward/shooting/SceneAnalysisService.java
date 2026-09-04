package com.frameforward.shooting;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.equipment.UserEquipmentService;
import com.frameforward.media.MediaEntity;
import com.frameforward.media.MediaManager;

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
    public AiTaskRuntime.Created create(String accessToken, String key, Request request) {
        business.validate(request);
        String accountId = auth.requireAccountId(accessToken);
        MediaEntity ownedMedia = media.findOwned(accountId, request.environmentMediaId);
        if (ownedMedia == null) {
            throw new MediaNotOwned();
        }
        List<UserEquipmentService.Item> ownedEquipment = equipment.list(accountId).stream()
                .filter(item -> request.equipmentIds.contains(item.id())).toList();
        if (ownedEquipment.size() != request.equipmentIds.size()) {
            throw new EquipmentNotOwned();
        }
        AiTaskRuntime.CreateRequest aiRequest = new AiTaskRuntime.CreateRequest();
        aiRequest.operationType = "scene-analysis";
        aiRequest.input = business.sceneInput(ownedMedia, ownedEquipment, request);
        AiTaskRuntime.Created task = tasks.create(accessToken, key, aiRequest);
        business.persistSceneIfAbsent(accountId, ownedMedia, ownedEquipment, request, task.taskId());
        return task;
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
