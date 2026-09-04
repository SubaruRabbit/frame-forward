package com.frameforward.generation;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.AiTaskCompletionProcessor;
import com.frameforward.ai.AiTaskEntity;

@Component
public class ReferenceImageCompletionProcessor extends AiTaskCompletionProcessor {
    private final ReferenceImageManager manager;
    public ReferenceImageCompletionProcessor(ReferenceImageManager manager) {
        this.manager = manager;
    }
    public boolean supports(String operationType) {
        return "reference-image-generation".equals(operationType);
    }
    @Transactional
    public void complete(AiTaskEntity task, Map<String, Object> result) {
        manager.complete(task, result);
    }
}
