package com.frameforward.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AiTaskConsumerMigrationTest {
    @Test
    void recoveryIntegrationTestUsesCanonicalRuntime() throws Exception {
        assertThat(AiTaskIntegrationTests.class.getDeclaredField("runtime").getType().getName())
                .isEqualTo("com.frameforward.ai.service.AiTaskRuntime");
    }
}
