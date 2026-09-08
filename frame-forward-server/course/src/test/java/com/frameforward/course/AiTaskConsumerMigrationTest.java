package com.frameforward.course;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
class AiTaskConsumerMigrationTest {
    @Test
    void servicesUseCanonicalRuntime() throws Exception {
        for (String name : new String[]{"CourseService"}) {
            Class<?> service = Class.forName("com.frameforward.course.service." + name);
            assertThat(java.util.Arrays.stream(service.getDeclaredFields()).map(field -> field.getType().getName()))
                    .contains("com.frameforward.ai.service.AiTaskRuntime")
                    .doesNotContain("com.frameforward.ai.AiTaskRuntime");
        }
    }
}
