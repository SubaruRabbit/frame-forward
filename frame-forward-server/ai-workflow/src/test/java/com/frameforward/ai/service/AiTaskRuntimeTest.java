package com.frameforward.ai.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.business.AiTaskBadRequest;
import com.frameforward.ai.business.AiTaskBusiness;
import com.frameforward.ai.business.AiTaskNotFound;
import com.frameforward.ai.business.PhotoEvaluationGraph;
import com.frameforward.ai.business.PlanGenerationGraph;
import com.frameforward.ai.business.ReferenceImageGraph;
import com.frameforward.ai.business.SceneAnalysisGraph;
import com.frameforward.ai.manager.AiTaskManager;
import com.frameforward.ai.mapper.AiTaskMapper;
import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskState;
import com.frameforward.ai.model.entity.AiTaskEntity;
import com.frameforward.auth.service.AuthService;

class AiTaskRuntimeTest {
    @Test
    void stateMachineAllowsOnlyDocumentedTransitions() {
        assertTrue(AiTaskRuntime.legal(AiTaskState.QUEUED, AiTaskState.RUNNING));
        assertTrue(AiTaskRuntime.legal(AiTaskState.RUNNING, AiTaskState.SUCCEEDED));
        assertTrue(AiTaskRuntime.legal(AiTaskState.RUNNING, AiTaskState.FAILED));
        assertFalse(AiTaskRuntime.legal(AiTaskState.QUEUED, AiTaskState.SUCCEEDED));
        assertFalse(AiTaskRuntime.legal(AiTaskState.SUCCEEDED, AiTaskState.RUNNING));
    }

    @Test
    void stateMachineRejectsEveryOtherTransition() {
        for (AiTaskState from : AiTaskState.values())
            for (AiTaskState to : AiTaskState.values()) {
                boolean expected = (from == AiTaskState.QUEUED
                        && (to == AiTaskState.RUNNING || to == AiTaskState.CANCELLED))
                        || (from == AiTaskState.RUNNING && (to == AiTaskState.SUCCEEDED || to == AiTaskState.FAILED
                                || to == AiTaskState.CANCELLED));
                assertEquals(expected, AiTaskRuntime.legal(from, to));
            }
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

        assertEquals(AiTaskState.QUEUED, created.state());
        verify(tasks).insert(any(AiTaskEntity.class));
        var existing = new AiTaskEntity();
        existing.id = "task-existing";
        existing.state = AiTaskState.QUEUED.name();
        when(tasks.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

        var duplicate = runtime.create("token", "request-1", request);

        assertEquals("task-existing", duplicate.taskId());
        verify(tasks, times(1)).insert(any(AiTaskEntity.class));
    }

    @Test
    void selectsConfiguredModelForEveryOperationRoute() {
        AiTaskMapper tasks = mock(AiTaskMapper.class);
        AuthService auth = mock(AuthService.class);
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(tasks.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        AiTaskRuntime runtime = runtime(tasks, auth);
        Map<String, String> routes = Map.of("scene-analysis", "scene", "photo-evaluation", "scene",
                "reference-image-generation", "image", "shooting-plan-generation", "qwen3.7-plus", "course-generation",
                "qwen3.7-plus", "coach", "coach", "other", "default");

        routes.forEach((operation, model) -> runtime.create("token", operation, request(operation)));

        ArgumentCaptor<AiTaskEntity> saved = ArgumentCaptor.forClass(AiTaskEntity.class);
        verify(tasks, times(routes.size())).insert(saved.capture());
        saved.getAllValues().forEach(task -> assertEquals(routes.get(task.operationType), task.modelId));
    }

    @Test
    void rejectsInvalidCreateRequestBeforeAuthentication() {
        var auth = mock(AuthService.class);

        assertThrows(AiTaskBadRequest.class,
                () -> runtime(mock(AiTaskMapper.class), auth).create("token", " ", request("coach")));

        verifyNoInteractions(auth);
    }

    @Test
    void recoversRunningTasksAsQueued() {
        AiTaskMapper tasks = mock(AiTaskMapper.class);
        AiTaskEntity interrupted = task("interrupted", AiTaskState.RUNNING, "scene-analysis", "{}");
        when(tasks.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(interrupted));

        runtime(tasks, mock(AuthService.class)).recoverInterruptedTasks();

        assertEquals(AiTaskState.QUEUED.name(), interrupted.state);
        verify(tasks).updateById(interrupted);
    }

    @Test
    void retriesInvalidModelOutputThreeTimesThenMarksTaskFailed() {
        AiTaskMapper tasks = mock(AiTaskMapper.class);
        AiTaskEntity queued = task("queued", AiTaskState.QUEUED, "scene-analysis", "{}");
        when(tasks.selectById("queued")).thenReturn(queued);
        Fixture fixture = runtimeFixture(tasks, mock(AuthService.class));
        when(fixture.sceneGraph.execute(Map.of())).thenReturn(Map.of());
        when(fixture.sceneGraph.valid(Map.of())).thenReturn(false);

        fixture.runtime.run("queued");

        assertEquals(AiTaskState.FAILED.name(), queued.state);
        assertEquals("INVALID_MODEL_OUTPUT", queued.errorCode);
        verify(fixture.sceneGraph, times(3)).execute(Map.of());
        verify(tasks, times(2)).updateById(queued);
    }

    @Test
    void returnsOwnedTaskStatusAndRejectsMissingOrForeignTask() {
        AiTaskMapper tasks = mock(AiTaskMapper.class);
        AuthService auth = mock(AuthService.class);
        AiTaskEntity owned = task("owned", AiTaskState.SUCCEEDED, "coach", "{}");
        owned.accountId = "account-1";
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(tasks.selectById("owned")).thenReturn(owned);

        assertEquals(AiTaskState.SUCCEEDED, runtime(tasks, auth).get("token", "owned").state());
        when(tasks.selectById("missing")).thenReturn(null);
        assertThrows(AiTaskNotFound.class, () -> runtime(tasks, auth).get("token", "missing"));
        owned.accountId = "another-account";
        assertThrows(AiTaskNotFound.class, () -> runtime(tasks, auth).get("token", "owned"));
    }

    @Test
    void completesEachSpecializedGraphThroughBusinessRules() {
        AiTaskMapper tasks = mock(AiTaskMapper.class);
        Fixture fixture = runtimeFixture(tasks, mock(AuthService.class));
        Map<String, Object> scene = Map.of("scene", "ok"), plan = Map.of("plan", "ok"),
                evaluation = Map.of("score", 80), reference = Map.of("image", "ok");
        when(fixture.sceneGraph.execute(Map.of())).thenReturn(scene);
        when(fixture.sceneGraph.valid(scene)).thenReturn(true);
        when(fixture.planGraph.execute(Map.of())).thenReturn(plan);
        when(fixture.planGraph.valid(plan)).thenReturn(true);
        when(fixture.evaluationGraph.execute(Map.of())).thenReturn(evaluation);
        when(fixture.evaluationGraph.valid(evaluation)).thenReturn(true);
        when(fixture.referenceImageGraph.execute(Map.of())).thenReturn(reference);
        when(fixture.referenceImageGraph.valid(reference)).thenReturn(true);
        AiTaskEntity sceneTask = task("scene", AiTaskState.QUEUED, "scene-analysis", "{}"),
                planTask = task("plan", AiTaskState.QUEUED, "shooting-plan-generation", "{}"),
                evaluationTask = task("evaluation", AiTaskState.QUEUED, "photo-evaluation", "{}"),
                referenceTask = task("reference", AiTaskState.QUEUED, "reference-image-generation", "{}");
        when(tasks.selectById("scene")).thenReturn(sceneTask);
        when(tasks.selectById("plan")).thenReturn(planTask);
        when(tasks.selectById("evaluation")).thenReturn(evaluationTask);
        when(tasks.selectById("reference")).thenReturn(referenceTask);

        fixture.runtime.run("scene");
        fixture.runtime.run("plan");
        fixture.runtime.run("evaluation");
        fixture.runtime.run("reference");

        assertEquals(AiTaskState.SUCCEEDED.name(), sceneTask.state);
        assertEquals(AiTaskState.SUCCEEDED.name(), planTask.state);
        assertEquals(AiTaskState.SUCCEEDED.name(), evaluationTask.state);
        assertEquals(AiTaskState.SUCCEEDED.name(), referenceTask.state);
    }

    @Test
    void marksTaskFailedWhenGraphExecutionThrows() {
        AiTaskMapper tasks = mock(AiTaskMapper.class);
        Fixture fixture = runtimeFixture(tasks, mock(AuthService.class));
        AiTaskEntity queued = task("error", AiTaskState.QUEUED, "scene-analysis", "{}");
        when(tasks.selectById("error")).thenReturn(queued);
        when(fixture.sceneGraph.execute(Map.of())).thenThrow(new IllegalStateException("model unavailable"));

        fixture.runtime.run("error");

        assertEquals(AiTaskState.FAILED.name(), queued.state);
        assertEquals("RUNTIME_ERROR", queued.errorCode);
    }

    @Test
    void returnsExistingTaskAfterIdempotencyConflictWithoutRescheduling() {
        AiTaskMapper tasks = mock(AiTaskMapper.class);
        AuthService auth = mock(AuthService.class);
        AiTaskEntity existing = task("existing", AiTaskState.QUEUED, "coach", "{}");
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(tasks.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null, existing);
        doThrow(new org.springframework.dao.DuplicateKeyException("duplicate")).when(tasks)
                .insert(any(AiTaskEntity.class));

        AiTaskCreated created = runtime(tasks, auth).create("token", "same-key", request("coach"));

        assertEquals("existing", created.taskId());
        assertEquals(AiTaskState.QUEUED, created.state());
    }

    @Test
    void completesGenericTaskFromStructuredMockOutput() throws Exception {
        AiTaskMapper tasks = mock(AiTaskMapper.class);
        Fixture fixture = runtimeFixture(tasks, mock(AuthService.class));
        AiTaskEntity queued = task("generic", AiTaskState.QUEUED, "coach",
                "{\"mockOutput\":{\"result\":\"accepted\"}}");
        when(tasks.selectById("generic")).thenReturn(queued);

        fixture.runtime.run("generic");

        assertEquals(AiTaskState.SUCCEEDED.name(), queued.state);
        assertEquals("accepted", new ObjectMapper().readTree(queued.resultJson).get("result").asText());
    }

    @Test
    void completesCourseGenerationUsingItsBuiltInValidatedOutput() {
        AiTaskMapper tasks = mock(AiTaskMapper.class);
        AiTaskEntity queued = task("course", AiTaskState.QUEUED, "course-generation", "{}");
        when(tasks.selectById("course")).thenReturn(queued);

        runtime(tasks, mock(AuthService.class)).run("course");

        assertEquals(AiTaskState.SUCCEEDED.name(), queued.state);
    }

    @Test
    void createsEmitterAndSendsCurrentOwnedStatus() {
        AiTaskMapper tasks = mock(AiTaskMapper.class);
        AuthService auth = mock(AuthService.class);
        AiTaskEntity task = task("stream", AiTaskState.QUEUED, "coach", "{}");
        task.accountId = "account-1";
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(tasks.selectById("stream")).thenReturn(task);

        SseEmitter emitter = runtime(tasks, auth).events("token", "stream");

        assertNotNull(emitter);
    }

    private static AiTaskRuntime runtime(AiTaskMapper tasks, AuthService auth) {
        return runtimeFixture(tasks, auth).runtime;
    }

    private static Fixture runtimeFixture(AiTaskMapper tasks, AuthService auth) {
        SceneAnalysisGraph sceneGraph = mock(SceneAnalysisGraph.class);
        PlanGenerationGraph planGraph = mock(PlanGenerationGraph.class);
        PhotoEvaluationGraph evaluationGraph = mock(PhotoEvaluationGraph.class);
        ReferenceImageGraph referenceImageGraph = mock(ReferenceImageGraph.class);
        AiTaskBusiness business = new AiTaskBusiness(
                new AiTaskManager(new com.frameforward.ai.repository.AiTaskRepository(tasks), List.of()),
                new ObjectMapper(), "default", "coach", "scene", "image", sceneGraph, planGraph, evaluationGraph,
                referenceImageGraph);
        return new Fixture(new AiTaskRuntime(business, auth, ignored -> {
        }), sceneGraph, planGraph, evaluationGraph, referenceImageGraph);
    }

    private static AiTaskEntity task(String id, AiTaskState state, String operationType, String inputJson) {
        AiTaskEntity task = new AiTaskEntity();
        task.id = id;
        task.state = state.name();
        task.operationType = operationType;
        task.inputJson = inputJson;
        task.workflowVersion = task.modelId = task.promptVersion = task.ruleVersion = task.schemaVersion = "v1";
        return task;
    }

    private static AiTaskCreateRequest request(String operationType) {
        var request = new AiTaskCreateRequest();
        request.operationType = operationType;
        return request;
    }

    private record Fixture(AiTaskRuntime runtime, SceneAnalysisGraph sceneGraph, PlanGenerationGraph planGraph,
            PhotoEvaluationGraph evaluationGraph, ReferenceImageGraph referenceImageGraph) {
    }
}
