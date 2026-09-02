package com.frameforward.shooting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;

class ShootingPlanServiceTest {
    @Test
    void createsPlanForOwnedScene() {
        var fixture = fixture();
        when(fixture.auth.requireAccountId("token")).thenReturn("account-1");
        when(fixture.scenes.selectOne(any(LambdaQueryWrapper.class))).thenReturn(scene());
        when(fixture.tasks.create(eq("token"), eq("key"), any(AiTaskRuntime.CreateRequest.class)))
                .thenReturn(new AiTaskRuntime.Created("task-1", AiTaskRuntime.State.QUEUED));

        var created = fixture.service.create("token", "key", request());

        assertEquals("task-1", created.taskId());
        verify(fixture.plans).insert(any(ShootingPlanEntity.class));
    }

    @Test
    void rejectsMissingRequestAndUnknownScene() {
        var missing = fixture();
        assertThrows(ShootingPlanService.InvalidRequest.class, () -> missing.service.create("token", "key", null));
        verifyNoInteractions(missing.auth);

        var unknown = fixture();
        when(unknown.auth.requireAccountId("token")).thenReturn("account-1");
        assertThrows(ShootingPlanService.SceneNotFound.class, () -> unknown.service.create("token", "key", request()));
    }

    @Test
    void doesNotPersistDuplicatePlanForExistingTask() {
        var fixture = fixture();
        when(fixture.auth.requireAccountId("token")).thenReturn("account-1");
        when(fixture.scenes.selectOne(any(LambdaQueryWrapper.class))).thenReturn(scene());
        when(fixture.tasks.create(eq("token"), eq("key"), any(AiTaskRuntime.CreateRequest.class)))
                .thenReturn(new AiTaskRuntime.Created("task-1", AiTaskRuntime.State.QUEUED));
        when(fixture.plans.selectOne(any(LambdaQueryWrapper.class))).thenReturn(new ShootingPlanEntity());

        fixture.service.create("token", "key", request());

        verify(fixture.plans, never()).insert(any(ShootingPlanEntity.class));
    }

    private static Fixture fixture() {
        var auth = mock(AuthService.class);
        var scenes = mock(SceneAnalysisMapper.class);
        var plans = mock(ShootingPlanMapper.class);
        var tasks = mock(AiTaskRuntime.class);
        return new Fixture(auth, scenes, plans, tasks,
                new ShootingPlanService(auth, scenes, plans, tasks, new ObjectMapper()));
    }

    private static SceneAnalysisEntity scene() {
        var scene = new SceneAnalysisEntity();
        scene.id = "scene-1";
        scene.subjectText = "人物";
        scene.targetStyle = "自然";
        scene.equipmentSnapshotJson = "[]";
        return scene;
    }

    private static ShootingPlanService.Request request() {
        var request = new ShootingPlanService.Request();
        request.sceneAnalysisId = "scene-1";
        return request;
    }

    private record Fixture(AuthService auth, SceneAnalysisMapper scenes, ShootingPlanMapper plans, AiTaskRuntime tasks,
            ShootingPlanService service) {
    }
}
