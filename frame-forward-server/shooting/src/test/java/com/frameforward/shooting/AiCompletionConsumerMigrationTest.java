package com.frameforward.shooting;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.model.dto.AiTaskCompletionContext;
import com.frameforward.shooting.service.SceneAnalysisCompletionProcessor;
class AiCompletionConsumerMigrationTest {
    @Test
    void callbackDirectlyImplementsCanonicalContextAndKeepsTransaction() throws Exception {
        assertThat(SceneAnalysisCompletionProcessor.class.getSuperclass().getName())
                .isEqualTo("com.frameforward.ai.gateway.AiTaskCompletionProcessor");
        assertThat(SceneAnalysisCompletionProcessor.class
                .getDeclaredMethod("complete", AiTaskCompletionContext.class, Map.class)
                .isAnnotationPresent(Transactional.class)).isTrue();
    }
}
