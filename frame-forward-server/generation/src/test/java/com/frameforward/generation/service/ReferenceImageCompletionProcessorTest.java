package com.frameforward.generation.service;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.model.dto.AiTaskCompletionContext;
import com.frameforward.generation.manager.ReferenceImageManager;
import com.frameforward.generation.model.entity.ReferenceImageEntity;
import com.frameforward.media.service.MediaService;
class ReferenceImageCompletionProcessorTest {
    @Test
    void keepsOperationSupportAndTransactionBoundary() throws Exception {
        var processor = new ReferenceImageCompletionProcessor(mock(ReferenceImageManager.class),
                mock(MediaService.class));
        assertThat(processor.supports("reference-image-generation")).isTrue();
        assertThat(processor.supports("scene-analysis")).isFalse();
        assertThat(processor.supports(null)).isFalse();
        assertThat(
                ReferenceImageCompletionProcessor.class.getMethod("complete", AiTaskCompletionContext.class, Map.class)
                        .isAnnotationPresent(Transactional.class))
                .isTrue();
    }
    @Test
    void mediaFailureDoesNotPersistOrPublishGeneratedResult() {
        var manager = mock(ReferenceImageManager.class);
        var media = mock(MediaService.class);
        var task = new AiTaskCompletionContext("task", "owner");
        var reference = new ReferenceImageEntity();
        reference.id = "reference";
        when(manager.findByTask("task")).thenReturn(reference);
        var failure = new IllegalStateException("media storage unavailable");
        when(media.registerGenerated("owner", "url", 10, 20)).thenThrow(failure);
        var result = new HashMap<String, Object>(Map.of("imageUrl", "url", "width", 10, "height", 20));
        assertThatThrownBy(() -> new ReferenceImageCompletionProcessor(manager, media).complete(task, result))
                .isSameAs(failure);
        verify(manager, never()).saveGeneratedMedia(any(), any());
        assertThat(reference.generatedMediaId).isNull();
        assertThat(result).doesNotContainKeys("referenceImageId", "mediaId");
    }
}
