package com.frameforward.shooting.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskState;
import com.frameforward.ai.service.AiTaskRuntime;
import com.frameforward.auth.service.AuthService;
import com.frameforward.shooting.business.ShootingBusiness;
import com.frameforward.shooting.business.ShootingPlanInvalidRequest;
import com.frameforward.shooting.business.ShootingPlanSceneNotFound;
import com.frameforward.shooting.manager.ShootingManager;
import com.frameforward.shooting.model.dto.ShootingPlanRequest;
import com.frameforward.shooting.model.entity.SceneAnalysisEntity;

class ShootingPlanServiceTest {
    @Test
    void createsPlanForOwnedScene() {
        var fixture = fixture();
        when(fixture.auth.requireAccountId("token")).thenReturn("account-1");
        when(fixture.manager.findOwnedScene("account-1", "scene-1")).thenReturn(scene());
        when(fixture.tasks.create(eq("token"), eq("key"), any(AiTaskCreateRequest.class)))
                .thenReturn(new AiTaskCreated("task-1", AiTaskState.QUEUED));

        var created = fixture.service.create("token", "key", request());

        assertEquals("task-1", created.taskId());
        verify(fixture.manager).persistPlanIfAbsent(eq("account-1"), any(SceneAnalysisEntity.class), eq("task-1"),
                anyString());
    }

    @Test
    void rejectsMissingRequestAndUnknownScene() {
        var missing = fixture();
        assertThrows(ShootingPlanInvalidRequest.class, () -> missing.service.create("token", "key", null));
        verifyNoInteractions(missing.auth);

        var unknown = fixture();
        when(unknown.auth.requireAccountId("token")).thenReturn("account-1");
        assertThrows(ShootingPlanSceneNotFound.class, () -> unknown.service.create("token", "key", request()));
    }

    @Test
    void doesNotPersistDuplicatePlanForExistingTask() {
        var fixture = fixture();
        when(fixture.auth.requireAccountId("token")).thenReturn("account-1");
        when(fixture.manager.findOwnedScene("account-1", "scene-1")).thenReturn(scene());
        when(fixture.tasks.create(eq("token"), eq("key"), any(AiTaskCreateRequest.class)))
                .thenReturn(new AiTaskCreated("task-1", AiTaskState.QUEUED));
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

    private static ShootingPlanRequest request() {
        var request = new ShootingPlanRequest();
        request.sceneAnalysisId = "scene-1";
        return request;
    }

    private record Fixture(AuthService auth, ShootingManager manager, AiTaskRuntime tasks,
            ShootingPlanService service) {
    }
}
