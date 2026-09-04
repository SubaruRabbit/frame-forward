package com.frameforward.shooting;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.AiTaskCompletionProcessor;
import com.frameforward.ai.AiTaskEntity;

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
    public void complete(AiTaskEntity task, Map<String, Object> result) {
        SceneAnalysisEntity scene = manager.findSceneForTask(task.id);
        if (scene == null || !task.accountId.equals(scene.accountId))
            throw new IllegalStateException("Missing owned scene analysis for completed task");
        result.put("sceneAnalysisId", scene.id);
    }
}
