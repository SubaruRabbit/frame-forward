package com.frameforward.generation;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.model.dto.AiTaskCompletionContext;
import com.frameforward.generation.service.ReferenceImageCompletionProcessor;
class AiCompletionConsumerMigrationTest {
    @Test
    void callbackDirectlyImplementsCanonicalContextAndKeepsTransaction() throws Exception {
        assertThat(ReferenceImageCompletionProcessor.class.getSuperclass().getName())
                .isEqualTo("com.frameforward.ai.gateway.AiTaskCompletionProcessor");
        assertThat(ReferenceImageCompletionProcessor.class
                .getDeclaredMethod("complete", AiTaskCompletionContext.class, Map.class)
                .isAnnotationPresent(Transactional.class)).isTrue();
    }
}
