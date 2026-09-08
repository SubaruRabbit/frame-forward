package com.frameforward.shooting.business;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.equipment.model.dto.UserEquipmentItem;
import com.frameforward.media.model.entity.MediaEntity;
import com.frameforward.shooting.manager.ShootingManager;
import com.frameforward.shooting.model.dto.SceneAnalysisRequest;
import com.frameforward.shooting.model.dto.ShootingPlanRequest;
import com.frameforward.shooting.model.entity.SceneAnalysisEntity;

@Component
public class ShootingBusiness {
    private final ShootingManager manager;
    private final ObjectMapper json;

    public ShootingBusiness(ShootingManager manager, ObjectMapper json) {
        this.manager = manager;
        this.json = json;
    }

    public void validate(SceneAnalysisRequest request) {
        if (!hasRequiredText(request) || !hasSupportedTimeConstraint(request) || !hasEquipmentIds(request)) {
            throw new SceneAnalysisInvalidRequest();
        }
    }

    public void validate(ShootingPlanRequest request) {
        if (request == null || request.sceneAnalysisId == null || request.sceneAnalysisId.isBlank()) {
            throw new ShootingPlanInvalidRequest();
        }
    }

    public Map<String, Object> sceneInput(MediaEntity media, List<UserEquipmentItem> equipment,
            SceneAnalysisRequest request) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("environmentMediaId", media.id);
        input.put("subjectType", request.subjectType);
        input.put("subject", request.subject);
        input.put("targetStyle", request.targetStyle);
        input.put("timeConstraintMinutes", request.timeConstraintMinutes);
        input.put("equipment", equipment);
        if (request.mockOutput != null) {
            input.put("mockOutput", request.mockOutput);
        }
        return input;
    }

    public void persistSceneIfAbsent(String accountId, MediaEntity media, List<UserEquipmentItem> equipment,
            SceneAnalysisRequest request, String taskId) {
        manager.persistSceneIfAbsent(accountId, media.id, request.subjectType, request.subject, request.targetStyle,
                request.timeConstraintMinutes, write(equipment), taskId);
    }

    public SceneAnalysisEntity findOwnedScene(String accountId, String sceneAnalysisId) {
        SceneAnalysisEntity scene = manager.findOwnedScene(accountId, sceneAnalysisId);
        if (scene == null) {
            throw new ShootingPlanSceneNotFound();
        }
        return scene;
    }

    public Map<String, Object> planInput(SceneAnalysisEntity scene, ShootingPlanRequest request) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("scene", Map.of("id", scene.id, "subject", scene.subjectText, "style", scene.targetStyle));
        input.put("equipmentSnapshot", read(scene.equipmentSnapshotJson));
        if (request.mockOutput != null) {
            input.put("mockOutput", request.mockOutput);
        }
        return input;
    }

    public void persistPlanIfAbsent(String accountId, SceneAnalysisEntity scene, String taskId) {
        manager.persistPlanIfAbsent(accountId, scene, taskId,
                write(Map.of("id", scene.id, "subject", scene.subjectText, "style", scene.targetStyle)));
    }

    private static boolean hasRequiredText(SceneAnalysisRequest request) {
        return request != null && !blank(request.environmentMediaId) && !blank(request.subjectType)
                && !blank(request.subject) && !blank(request.targetStyle);
    }

    private static boolean hasSupportedTimeConstraint(SceneAnalysisRequest request) {
        return request != null && request.timeConstraintMinutes != null && request.timeConstraintMinutes >= 1
                && request.timeConstraintMinutes <= 1440;
    }

    private static boolean hasEquipmentIds(SceneAnalysisRequest request) {
        return request != null && request.equipmentIds != null;
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private Object read(String value) {
        try {
            return json.readValue(value, Object.class);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private String write(Object value) {
        try {
            return json.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
