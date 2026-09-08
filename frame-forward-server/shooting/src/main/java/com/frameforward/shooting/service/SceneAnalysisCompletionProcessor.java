package com.frameforward.shooting.service;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.gateway.AiTaskCompletionProcessor;
import com.frameforward.ai.model.dto.AiTaskCompletionContext;
import com.frameforward.shooting.manager.ShootingManager;
import com.frameforward.shooting.model.entity.SceneAnalysisEntity;

@Component
public class SceneAnalysisCompletionProcessor extends AiTaskCompletionProcessor {
    private final ShootingManager manager;

    public SceneAnalysisCompletionProcessor(ShootingManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean supports(String operationType) {
        return "scene-analysis".equals(operationType);
    }

    @Override
    @Transactional
    public void complete(AiTaskCompletionContext task, Map<String, Object> result) {
        SceneAnalysisEntity scene = manager.findSceneForTask(task.taskId());
        if (scene == null || !task.accountId().equals(scene.accountId))
            throw new IllegalStateException("Missing owned scene analysis for completed task");
        result.put("sceneAnalysisId", scene.id);
    }
}
