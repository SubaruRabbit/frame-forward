package com.frameforward.ai.model.dto;

import java.util.Map;

public record AiTaskStatus(String taskId, AiTaskState state, Map<String, Object> result, String errorCode,
        AiTaskTrace trace) {
}
