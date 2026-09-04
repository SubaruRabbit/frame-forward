package com.frameforward.ai;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class AiWorkflowArchitectureTest {

    @Test
    void runtimeDoesNotDependOnMapper() {
        assertFalse(Arrays.stream(AiTaskRuntime.class.getDeclaredFields()).map(field -> field.getType())
                .anyMatch(AiTaskMapper.class::equals), "AiTaskRuntime must delegate persistence to AiTaskManager");
    }
}
