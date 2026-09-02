package com.frameforward.ai;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AiTaskRuntimeTest {
    @Test
    void stateMachineAllowsOnlyDocumentedTransitions() {
        assertTrue(AiTaskRuntime.legal(AiTaskRuntime.State.QUEUED, AiTaskRuntime.State.RUNNING));
        assertTrue(AiTaskRuntime.legal(AiTaskRuntime.State.RUNNING, AiTaskRuntime.State.SUCCEEDED));
        assertTrue(AiTaskRuntime.legal(AiTaskRuntime.State.RUNNING, AiTaskRuntime.State.FAILED));
        assertFalse(AiTaskRuntime.legal(AiTaskRuntime.State.QUEUED, AiTaskRuntime.State.SUCCEEDED));
        assertFalse(AiTaskRuntime.legal(AiTaskRuntime.State.SUCCEEDED, AiTaskRuntime.State.RUNNING));
    }
}
