package com.frameforward.shooting.manager;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.frameforward.shooting.model.entity.SceneAnalysisEntity;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;
import com.frameforward.shooting.repository.ShootingRepository;

@Component
public class ShootingManager {
    private final ShootingRepository repository;

    public ShootingManager(ShootingRepository repository) {
        this.repository = repository;
    }

    public void persistSceneIfAbsent(String accountId, String environmentMediaId, String subjectType, String subject,
            String targetStyle, Integer timeConstraintMinutes, String equipmentSnapshotJson, String taskId) {
        if (repository.findSceneForTask(taskId) != null) {
            return;
        }
        SceneAnalysisEntity analysis = new SceneAnalysisEntity();
        analysis.id = UUID.randomUUID().toString();
        analysis.accountId = accountId;
        analysis.environmentMediaId = environmentMediaId;
        analysis.subjectType = subjectType;
        analysis.subjectText = subject;
        analysis.targetStyle = targetStyle;
        analysis.timeConstraintMinutes = timeConstraintMinutes;
        analysis.equipmentSnapshotJson = equipmentSnapshotJson;
        analysis.aiTaskId = taskId;
        analysis.createdAt = Instant.now();
        repository.saveScene(analysis);
    }

    public SceneAnalysisEntity findOwnedScene(String accountId, String sceneAnalysisId) {
        return repository.findOwnedScene(accountId, sceneAnalysisId);
    }

    public SceneAnalysisEntity findSceneForTask(String taskId) {
        return repository.findSceneForTask(taskId);
    }

    public void persistPlanIfAbsent(String accountId, SceneAnalysisEntity scene, String taskId,
            String sceneSnapshotJson) {
        if (repository.findPlanForTask(taskId) != null) {
            return;
        }
        ShootingPlanEntity plan = new ShootingPlanEntity();
        plan.id = UUID.randomUUID().toString();
        plan.accountId = accountId;
        plan.sceneAnalysisId = scene.id;
        plan.aiTaskId = taskId;
        plan.sceneSnapshotJson = sceneSnapshotJson;
        plan.equipmentSnapshotJson = scene.equipmentSnapshotJson;
        plan.createdAt = Instant.now();
        repository.savePlan(plan);
    }
}
