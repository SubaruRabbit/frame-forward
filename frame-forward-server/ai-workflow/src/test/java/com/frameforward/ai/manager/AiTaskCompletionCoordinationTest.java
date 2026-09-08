package com.frameforward.ai.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.frameforward.ai.gateway.AiTaskCompletionProcessor;
import com.frameforward.ai.model.dto.AiTaskCompletionContext;
import com.frameforward.ai.model.entity.AiTaskEntity;
import com.frameforward.ai.repository.AiTaskRepository;

class AiTaskCompletionCoordinationTest {
    @Test
    void selectsMatchingCallbacksAndPreservesContextAndResultIdentity() {
        var matching = mock(AiTaskCompletionProcessor.class);
        var other = mock(AiTaskCompletionProcessor.class);
        when(matching.supports("scene-analysis")).thenReturn(true);
        var result = new HashMap<String, Object>();
        doAnswer(call -> {
            assertThat(call.<AiTaskCompletionContext>getArgument(0))
                    .isEqualTo(new AiTaskCompletionContext("task", "account"));
            assertThat(call.<Map<String, Object>>getArgument(1)).isSameAs(result);
            result.put("sceneAnalysisId", "scene");
            return null;
        }).when(matching).complete(any(), any());
        new AiTaskManager(mock(AiTaskRepository.class), List.of(other, matching)).completeResults(task(), result);
        assertThat(result).containsEntry("sceneAnalysisId", "scene");
        verify(matching).complete(any(), same(result));
        verify(other, never()).complete(any(), any());
    }

    @Test
    void propagatesCallbackFailureWithoutPersistingSuccess() {
        var callback = mock(AiTaskCompletionProcessor.class);
        when(callback.supports("scene-analysis")).thenReturn(true);
        var failure = new IllegalStateException("failed");
        doThrow(failure).when(callback).complete(any(), any());
        var repository = mock(AiTaskRepository.class);
        var manager = new AiTaskManager(repository, List.of(callback));
        assertThatThrownBy(() -> manager.completeResults(task(), new HashMap<>())).isSameAs(failure);
        verifyNoInteractions(repository);
    }

    private static AiTaskEntity task() {
        var task = new AiTaskEntity();
        task.id = "task";
        task.accountId = "account";
        task.operationType = "scene-analysis";
        return task;
    }
}
