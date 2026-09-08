package com.frameforward.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.business.AiTaskBadRequest;
import com.frameforward.ai.business.AiTaskBusiness;
import com.frameforward.ai.business.AiTaskNotFound;
import com.frameforward.ai.model.dto.*;
import com.frameforward.ai.model.entity.AiTaskEntity;
import com.frameforward.auth.service.AuthService;

class AiTaskRuntimeContractTest {
    private final AiTaskBusiness business = mock(AiTaskBusiness.class);
    private final AuthService auth = mock(AuthService.class);
    private final Executor executor = mock(Executor.class);
    private final AiTaskRuntime runtime = new AiTaskRuntime(business, auth, executor);

    @BeforeEach
    void owner() {
        when(auth.requireAccountId("token")).thenReturn("account");
    }

    @Test
    void preservesCreatedJsonAndRequestDataForEveryState() {
        var json = new ObjectMapper();
        var request = request();
        for (AiTaskState state : AiTaskState.values()) {
            var created = new AiTaskCreated("task", state);
            when(business.create("account", "key", request)).thenReturn(new AiTaskCreation(created, false));
            var result = runtime.create("token", "key", request);
            assertThat(result).isSameAs(created);
            assertThat(json.<JsonNode>valueToTree(result))
                    .isEqualTo(json.valueToTree(Map.of("taskId", "task", "state", state.name())));
        }
        verify(business, times(5)).create("account", "key", request);
        assertThat(request.input).isEqualTo(Map.of("question", "构图"));
        verifyNoInteractions(executor);
    }

    @Test
    void preservesTraceResultAndNullableJsonFields() throws Exception {
        var task = new AiTaskEntity();
        when(business.findTask("task")).thenReturn(task);
        var trace = new AiTaskTrace("workflow", "model", "prompt", "rule", "schema");
        var status = new AiTaskStatus("task", AiTaskState.SUCCEEDED, Map.of("result", "accepted"), null, trace);
        when(business.statusForOwner(task, "account")).thenReturn(status);
        var json = new ObjectMapper();
        assertThat(json.<JsonNode>valueToTree(runtime.get("token", "task"))).isEqualTo(json.readTree(
                """
                        {"taskId":"task","state":"SUCCEEDED","result":{"result":"accepted"},"errorCode":null,
                         "trace":{"workflowVersion":"workflow","modelId":"model","promptVersion":"prompt","ruleVersion":"rule","schemaVersion":"schema"}}
                        """));
        when(business.statusForOwner(task, "account"))
                .thenReturn(new AiTaskStatus("task", AiTaskState.FAILED, null, "RUNTIME_ERROR", null));
        assertThat(json.<JsonNode>valueToTree(runtime.get("token", "task"))).isEqualTo(json.readTree("""
                {"taskId":"task","state":"FAILED","result":null,"errorCode":"RUNTIME_ERROR","trace":null}
                """));
    }

    @Test
    void validatesNullBeforeAuthenticationAndKeepsMissingTaskPriority() {
        assertThatThrownBy(() -> runtime.create("token", "key", null)).isInstanceOf(AiTaskBadRequest.class);
        verifyNoInteractions(auth, business, executor);
        assertThatThrownBy(() -> runtime.get("token", "missing")).isInstanceOf(AiTaskNotFound.class);
        verifyNoInteractions(auth);
    }

    @Test
    void propagatesAuthenticationAndOwnershipFailuresWithoutWrapping() {
        var authentication = new AuthService.InvalidSessionException();
        when(auth.requireAccountId("token")).thenThrow(authentication);
        assertThatThrownBy(() -> runtime.create("token", "key", request())).isSameAs(authentication);
        doReturn("account").when(auth).requireAccountId("token");
        var task = new AiTaskEntity();
        var ownership = new AiTaskNotFound();
        when(business.findTask("task")).thenReturn(task);
        when(business.statusForOwner(task, "account")).thenThrow(ownership);
        assertThatThrownBy(() -> runtime.get("token", "task")).isSameAs(ownership);
    }

    @Test
    void schedulesOnlyAfterTransactionCommit() {
        var request = request();
        when(business.create("account", "key", request))
                .thenReturn(new AiTaskCreation(new AiTaskCreated("task", AiTaskState.QUEUED), true));
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);
        try {
            runtime.create("token", "key", request);
            verifyNoInteractions(executor);
            var callbacks = TransactionSynchronizationManager.getSynchronizations();
            assertThat(callbacks).hasSize(1);
            callbacks.getFirst().afterCommit();
            var runnable = ArgumentCaptor.forClass(Runnable.class);
            verify(executor).execute(runnable.capture());
            runnable.getValue().run();
            verify(business).run(eq("task"), any());
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }
    }

    @Test
    void recoveryHasOneLifecycleHookAndEventsUseCurrentOwnedStatus() throws Exception {
        runtime.recoverInterruptedTasks();
        verify(business).recoverInterruptedTasks();
        var method = AiTaskRuntime.class.getMethod("recoverInterruptedTasks");
        assertThat(method.isAnnotationPresent(jakarta.annotation.PostConstruct.class)).isTrue();
        assertThat(method.isAnnotationPresent(Transactional.class)).isTrue();
        var task = new AiTaskEntity();
        when(business.findTask("task")).thenReturn(task);
        when(business.statusForOwner(task, "account"))
                .thenReturn(new AiTaskStatus("task", AiTaskState.QUEUED, null, null, null));
        assertThat(runtime.events("token", "task").getTimeout()).isZero();
        verify(business).statusForOwner(task, "account");
    }

    private static AiTaskCreateRequest request() {
        var request = new AiTaskCreateRequest();
        request.operationType = "coach";
        request.input = Map.of("question", "构图");
        return request;
    }
}
