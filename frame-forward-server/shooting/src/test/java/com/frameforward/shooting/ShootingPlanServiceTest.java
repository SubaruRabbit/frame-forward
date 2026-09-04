package com.frameforward.shooting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;

class ShootingPlanServiceTest {
    @Test
    void createsPlanForOwnedScene() {
        var fixture = fixture();
        when(fixture.auth.requireAccountId("token")).thenReturn("account-1");
        when(fixture.manager.findOwnedScene("account-1", "scene-1")).thenReturn(scene());
        when(fixture.tasks.create(eq("token"), eq("key"), any(AiTaskRuntime.CreateRequest.class)))
                .thenReturn(new AiTaskRuntime.Created("task-1", AiTaskRuntime.State.QUEUED));

        var created = fixture.service.create("token", "key", request());

        assertEquals("task-1", created.taskId());
        verify(fixture.manager).persistPlanIfAbsent(eq("account-1"), any(SceneAnalysisEntity.class), eq("task-1"),
                anyString());
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
        when(fixture.manager.findOwnedScene("account-1", "scene-1")).thenReturn(scene());
        when(fixture.tasks.create(eq("token"), eq("key"), any(AiTaskRuntime.CreateRequest.class)))
                .thenReturn(new AiTaskRuntime.Created("task-1", AiTaskRuntime.State.QUEUED));
        fixture.service.create("token", "key", request());

        verify(fixture.manager).persistPlanIfAbsent(eq("account-1"), any(SceneAnalysisEntity.class), eq("task-1"),
                anyString());
    }

    private static Fixture fixture() {
        var auth = mock(AuthService.class);
        var manager = mock(ShootingManager.class);
        var tasks = mock(AiTaskRuntime.class);
        var business = new ShootingBusiness(manager, new ObjectMapper());
        return new Fixture(auth, manager, tasks, new ShootingPlanService(auth, tasks, business));
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

    private record Fixture(AuthService auth, ShootingManager manager, AiTaskRuntime tasks,
            ShootingPlanService service) {
    }
}
