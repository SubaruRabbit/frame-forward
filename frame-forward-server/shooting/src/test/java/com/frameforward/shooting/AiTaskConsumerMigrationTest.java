package com.frameforward.shooting;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
class AiTaskConsumerMigrationTest {
    @Test
    void servicesUseCanonicalRuntime() throws Exception {
        for (String name : new String[]{"SceneAnalysisService", "ShootingPlanService"}) {
            Class<?> service = Class.forName("com.frameforward.shooting.service." + name);
            assertThat(java.util.Arrays.stream(service.getDeclaredFields()).map(field -> field.getType().getName()))
                    .contains("com.frameforward.ai.service.AiTaskRuntime")
                    .doesNotContain("com.frameforward.ai.AiTaskRuntime");
        }
    }
}
