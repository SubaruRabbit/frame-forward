package com.frameforward.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.auth.AuthService;

class AiTaskRuntimeTest {
    @Test
    void stateMachineAllowsOnlyDocumentedTransitions() {
        assertTrue(AiTaskRuntime.legal(AiTaskRuntime.State.QUEUED, AiTaskRuntime.State.RUNNING));
        assertTrue(AiTaskRuntime.legal(AiTaskRuntime.State.RUNNING, AiTaskRuntime.State.SUCCEEDED));
        assertTrue(AiTaskRuntime.legal(AiTaskRuntime.State.RUNNING, AiTaskRuntime.State.FAILED));
        assertFalse(AiTaskRuntime.legal(AiTaskRuntime.State.QUEUED, AiTaskRuntime.State.SUCCEEDED));
        assertFalse(AiTaskRuntime.legal(AiTaskRuntime.State.SUCCEEDED, AiTaskRuntime.State.RUNNING));
    }

    @Test
    void createsNewTaskAndReturnsExistingTaskForSameIdempotencyKey() {
        var tasks = mock(AiTaskMapper.class);
        var auth = mock(AuthService.class);
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(tasks.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        var runtime = runtime(tasks, auth);
        var request = request("coach");

        var created = runtime.create("token", "request-1", request);

        assertEquals(AiTaskRuntime.State.QUEUED, created.state());
        verify(tasks).insert(any(AiTaskEntity.class));
        var existing = new AiTaskEntity();
        existing.id = "task-existing";
        existing.state = AiTaskRuntime.State.QUEUED.name();
        when(tasks.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

        var duplicate = runtime.create("token", "request-1", request);

        assertEquals("task-existing", duplicate.taskId());
        verify(tasks, times(1)).insert(any(AiTaskEntity.class));
    }

    @Test
    void rejectsInvalidCreateRequestBeforeAuthentication() {
        var auth = mock(AuthService.class);

        assertThrows(AiTaskRuntime.BadRequest.class,
                () -> runtime(mock(AiTaskMapper.class), auth).create("token", " ", request("coach")));

        verifyNoInteractions(auth);
    }

    private static AiTaskRuntime runtime(AiTaskMapper tasks, AuthService auth) {
        return new AiTaskRuntime(tasks, new ObjectMapper(), auth, ignored -> {
        }, "default", "coach", "scene", "image", mock(SceneAnalysisGraph.class), mock(PlanGenerationGraph.class),
                mock(PhotoEvaluationGraph.class), mock(ReferenceImageGraph.class), java.util.List.of());
    }

    private static AiTaskRuntime.CreateRequest request(String operationType) {
        var request = new AiTaskRuntime.CreateRequest();
        request.operationType = operationType;
        return request;
    }
}
