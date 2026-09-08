package com.frameforward.shooting.repository;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.shooting.mapper.SceneAnalysisMapper;
import com.frameforward.shooting.mapper.ShootingPlanMapper;
import com.frameforward.shooting.model.entity.SceneAnalysisEntity;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;

@Repository
public class ShootingRepository {
    private final SceneAnalysisMapper scenes;
    private final ShootingPlanMapper plans;

    public ShootingRepository(SceneAnalysisMapper scenes, ShootingPlanMapper plans) {
        this.scenes = scenes;
        this.plans = plans;
    }

    public SceneAnalysisEntity findSceneForTask(String taskId) {
        return scenes
                .selectOne(new LambdaQueryWrapper<SceneAnalysisEntity>().eq(SceneAnalysisEntity::getAiTaskId, taskId));
    }

    public SceneAnalysisEntity findOwnedScene(String accountId, String sceneAnalysisId) {
        return scenes.selectOne(new LambdaQueryWrapper<SceneAnalysisEntity>()
                .eq(SceneAnalysisEntity::getId, sceneAnalysisId).eq(SceneAnalysisEntity::getAccountId, accountId));
    }

    public ShootingPlanEntity findPlanForTask(String taskId) {
        return plans
                .selectOne(new LambdaQueryWrapper<ShootingPlanEntity>().eq(ShootingPlanEntity::getAiTaskId, taskId));
    }

    public void saveScene(SceneAnalysisEntity scene) {
        scenes.insert(scene);
    }

    public void savePlan(ShootingPlanEntity plan) {
        plans.insert(plan);
    }
}
