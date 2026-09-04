package com.frameforward.shooting;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Component
class ShootingManager {
    private final SceneAnalysisMapper scenes;
    private final ShootingPlanMapper plans;

    ShootingManager(SceneAnalysisMapper scenes, ShootingPlanMapper plans) {
        this.scenes = scenes;
        this.plans = plans;
    }

    void persistSceneIfAbsent(String accountId, String environmentMediaId, String subjectType, String subject,
            String targetStyle, Integer timeConstraintMinutes, String equipmentSnapshotJson, String taskId) {
        if (scenes.selectOne(
                new LambdaQueryWrapper<SceneAnalysisEntity>().eq(SceneAnalysisEntity::getAiTaskId, taskId)) != null) {
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
        scenes.insert(analysis);
    }

    SceneAnalysisEntity findOwnedScene(String accountId, String sceneAnalysisId) {
        return scenes.selectOne(new LambdaQueryWrapper<SceneAnalysisEntity>()
                .eq(SceneAnalysisEntity::getId, sceneAnalysisId).eq(SceneAnalysisEntity::getAccountId, accountId));
    }

    SceneAnalysisEntity findSceneForTask(String taskId) {
        return scenes
                .selectOne(new LambdaQueryWrapper<SceneAnalysisEntity>().eq(SceneAnalysisEntity::getAiTaskId, taskId));
    }

    void persistPlanIfAbsent(String accountId, SceneAnalysisEntity scene, String taskId, String sceneSnapshotJson) {
        if (plans.selectOne(
                new LambdaQueryWrapper<ShootingPlanEntity>().eq(ShootingPlanEntity::getAiTaskId, taskId)) != null) {
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
        plans.insert(plan);
    }
}
