package com.frameforward.ai;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.frameforward.ai.business.AiTaskBusiness;
import com.frameforward.ai.manager.AiTaskManager;

class AiCompletionBoundaryMigrationTest {
    @Test
    void gatewayUsesContextAndBusinessDelegatesCompletionToManager() throws Exception {
        Class<?> gateway = Class.forName("com.frameforward.ai.gateway.AiTaskCompletionProcessor");
        Class<?> context = Class.forName("com.frameforward.ai.model.dto.AiTaskCompletionContext");
        assertThat(gateway.getMethod("complete", context, Map.class).getReturnType()).isEqualTo(void.class);
        assertThat(context.isRecord()).isTrue();
        assertThat(Arrays.stream(AiTaskBusiness.class.getDeclaredFields()).map(field -> field.getName()))
                .doesNotContain("completionProcessors");
        assertThat(AiTaskManager.class.getDeclaredField("completionProcessors").getGenericType().getTypeName())
                .contains(gateway.getName());
    }
}
