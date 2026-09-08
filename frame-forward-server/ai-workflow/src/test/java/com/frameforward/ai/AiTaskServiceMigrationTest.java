package com.frameforward.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AiTaskServiceMigrationTest {
    @Test
    void canonicalServiceExposesIndependentDataTypes() throws Exception {
        Class<?> service = Class.forName("com.frameforward.ai.service.AiTaskRuntime");
        Class<?> request = Class.forName("com.frameforward.ai.model.dto.AiTaskCreateRequest");
        assertThat(service.getMethod("create", String.class, String.class, request).getReturnType().getName())
                .isEqualTo("com.frameforward.ai.model.dto.AiTaskCreated");
        assertThat(service.getMethod("get", String.class, String.class).getReturnType().getName())
                .isEqualTo("com.frameforward.ai.model.dto.AiTaskStatus");
        assertThat(request.getEnclosingClass()).isNull();
    }
}
