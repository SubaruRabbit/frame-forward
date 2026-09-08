package com.frameforward.shooting;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.media.model.entity.MediaEntity;
import com.frameforward.shooting.business.SceneAnalysisInvalidRequest;
import com.frameforward.shooting.business.ShootingBusiness;
import com.frameforward.shooting.business.ShootingPlanInvalidRequest;
import com.frameforward.shooting.business.ShootingPlanSceneNotFound;
import com.frameforward.shooting.manager.ShootingManager;
import com.frameforward.shooting.mapper.SceneAnalysisMapper;
import com.frameforward.shooting.mapper.ShootingPlanMapper;
import com.frameforward.shooting.model.dto.SceneAnalysisRequest;
import com.frameforward.shooting.model.dto.ShootingPlanRequest;
import com.frameforward.shooting.model.entity.SceneAnalysisEntity;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;
import com.frameforward.shooting.repository.ShootingRepository;
import com.frameforward.shooting.service.SceneAnalysisCompletionProcessor;

class ShootingLayerTest {
    @Test
    void managerPersistsOnlyAbsentSceneAndPlan() {
        var scenes = mock(SceneAnalysisMapper.class);
        var plans = mock(ShootingPlanMapper.class);
        var manager = new ShootingManager(new ShootingRepository(scenes, plans));
        when(scenes.selectOne(any())).thenReturn(null);
        manager.persistSceneIfAbsent("account", "media", "portrait", "subject", "style", 30, "[]", "task");
        verify(scenes).insert(any(SceneAnalysisEntity.class));
        when(scenes.selectOne(any())).thenReturn(new SceneAnalysisEntity());
        manager.persistSceneIfAbsent("account", "media", "portrait", "subject", "style", 30, "[]", "task");
        verify(scenes, times(1)).insert(any(SceneAnalysisEntity.class));
        assertNotNull(manager.findOwnedScene("account", "scene"));
        var sceneForTask = new SceneAnalysisEntity();
        when(scenes.selectOne(any())).thenReturn(sceneForTask);
        assertSame(sceneForTask, manager.findSceneForTask("task"));
        var scene = new SceneAnalysisEntity();
        scene.id = "scene";
        scene.equipmentSnapshotJson = "[]";
        when(plans.selectOne(any())).thenReturn(null);
        manager.persistPlanIfAbsent("account", scene, "task", "{}");
        verify(plans).insert(any(ShootingPlanEntity.class));
        when(plans.selectOne(any())).thenReturn(new ShootingPlanEntity());
        manager.persistPlanIfAbsent("account", scene, "task", "{}");
        verify(plans, times(1)).insert(any(ShootingPlanEntity.class));
    }

    @Test
    void businessValidatesAndBuildsInputsAndReportsMissingScene() {
        var manager = mock(ShootingManager.class);
        var business = new ShootingBusiness(manager, new ObjectMapper());
        var sceneRequest = new SceneAnalysisRequest();
        sceneRequest.environmentMediaId = "media";
        sceneRequest.subjectType = "portrait";
        sceneRequest.subject = "person";
        sceneRequest.targetStyle = "natural";
        sceneRequest.timeConstraintMinutes = 30;
        sceneRequest.equipmentIds = List.of();
        business.validate(sceneRequest);
        sceneRequest.timeConstraintMinutes = 0;
        assertThrows(SceneAnalysisInvalidRequest.class, () -> business.validate(sceneRequest));
        sceneRequest.timeConstraintMinutes = 30;
        var media = new MediaEntity();
        media.id = "media";
        assertEquals("media", business.sceneInput(media, List.of(), sceneRequest).get("environmentMediaId"));
        sceneRequest.mockOutput = Map.of("ok", true);
        assertEquals(Map.of("ok", true), business.sceneInput(media, List.of(), sceneRequest).get("mockOutput"));
        business.persistSceneIfAbsent("account", media, List.of(), sceneRequest, "task");
        verify(manager).persistSceneIfAbsent(eq("account"), eq("media"), any(), any(), any(), any(), any(), eq("task"));
        when(manager.findOwnedScene("account", "scene")).thenReturn(null);
        assertThrows(ShootingPlanSceneNotFound.class, () -> business.findOwnedScene("account", "scene"));
        var scene = new SceneAnalysisEntity();
        scene.id = "scene";
        scene.subjectText = "person";
        scene.targetStyle = "natural";
        scene.equipmentSnapshotJson = "[]";
        when(manager.findOwnedScene("account", "scene")).thenReturn(scene);
        assertSame(scene, business.findOwnedScene("account", "scene"));
        var planRequest = new ShootingPlanRequest();
        planRequest.sceneAnalysisId = "scene";
        business.validate(planRequest);
        planRequest.sceneAnalysisId = " ";
        assertThrows(ShootingPlanInvalidRequest.class, () -> business.validate(planRequest));
        planRequest.sceneAnalysisId = "scene";
        planRequest.mockOutput = Map.of("ok", true);
        assertEquals(Map.of("ok", true), business.planInput(scene, planRequest).get("mockOutput"));
        business.persistPlanIfAbsent("account", scene, "task");
        verify(manager).persistPlanIfAbsent(eq("account"), eq(scene), eq("task"), any());
    }

    @Test
    void completionProcessorReturnsOnlyTheOwnedPersistedSceneId() {
        var manager = mock(ShootingManager.class);
        var processor = new SceneAnalysisCompletionProcessor(manager);
        var task = new com.frameforward.ai.model.dto.AiTaskCompletionContext("task", "account");
        var scene = new SceneAnalysisEntity();
        scene.id = "scene";
        scene.accountId = "account";
        when(manager.findSceneForTask("task")).thenReturn(scene);
        var result = new HashMap<String, Object>();

        assertTrue(processor.supports("scene-analysis"));
        assertFalse(processor.supports("shooting-plan-generation"));
        processor.complete(task, result);

        assertEquals("scene", result.get("sceneAnalysisId"));
        when(manager.findSceneForTask("task")).thenReturn(null);
        assertThrows(IllegalStateException.class, () -> processor.complete(task, new HashMap<>()));
        scene.accountId = "other-account";
        when(manager.findSceneForTask("task")).thenReturn(scene);
        assertThrows(IllegalStateException.class, () -> processor.complete(task, new HashMap<>()));
    }
}
