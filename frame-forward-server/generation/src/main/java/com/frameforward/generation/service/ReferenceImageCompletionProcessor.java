package com.frameforward.generation.service;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.gateway.AiTaskCompletionProcessor;
import com.frameforward.ai.model.dto.AiTaskCompletionContext;
import com.frameforward.generation.manager.ReferenceImageManager;
import com.frameforward.media.service.MediaService;

@Component
public class ReferenceImageCompletionProcessor extends AiTaskCompletionProcessor {
    private final ReferenceImageManager manager;
    private final MediaService media;
    public ReferenceImageCompletionProcessor(ReferenceImageManager manager, MediaService media) {
        this.manager = manager;
        this.media = media;
    }
    public boolean supports(String operationType) {
        return "reference-image-generation".equals(operationType);
    }
    @Transactional
    public void complete(AiTaskCompletionContext task, Map<String, Object> result) {
        var reference = manager.findByTask(task.taskId());
        if (reference == null || reference.generatedMediaId != null) {
            return;
        }
        var generated = media.registerGenerated(task.accountId(), String.valueOf(result.get("imageUrl")),
                ((Number) result.get("width")).intValue(), ((Number) result.get("height")).intValue());
        manager.saveGeneratedMedia(reference, generated.id());
        result.put("referenceImageId", reference.id);
        result.put("mediaId", generated.id());
    }
}
