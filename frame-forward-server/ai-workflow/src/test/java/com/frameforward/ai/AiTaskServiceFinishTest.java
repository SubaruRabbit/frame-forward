package com.frameforward.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

class AiTaskServiceFinishTest {
    @Test
    void actualServiceReplacesOldEntryAndCompatibilityDelegate() throws Exception {
        assertThat(Files.exists(Path.of("src/main/java/com/frameforward/ai/AiTaskRuntime.java"))).isFalse();
        Class<?> runtime = Class.forName("com.frameforward.ai.service.AiTaskRuntime");
        assertThat(runtime.getAnnotation(Service.class).value()).isEmpty();
        assertThat(Arrays.stream(runtime.getDeclaredFields()).map(field -> field.getName())).doesNotContain("delegate");
        Class<?> business = Class.forName("com.frameforward.ai.business.AiTaskBusiness");
        assertThat(Arrays.stream(business.getDeclaredFields()).map(field -> field.getType().getName())).doesNotContain(
                "com.frameforward.auth.service.AuthService", "com.frameforward.ai.service.AiTaskRuntime",
                "com.frameforward.ai.mapper.AiTaskMapper");
    }
}
