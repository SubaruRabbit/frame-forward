package com.frameforward.ai.model.dto;

public record AiTaskTrace(String workflowVersion, String modelId, String promptVersion, String ruleVersion,
        String schemaVersion) {
}
