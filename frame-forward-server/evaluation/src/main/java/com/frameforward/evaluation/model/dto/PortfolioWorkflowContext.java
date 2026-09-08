package com.frameforward.evaluation.model.dto;
import java.util.List;
import java.util.Map;
public record PortfolioWorkflowContext(String mediaId, Map<String, Object> evaluation, Map<String, Object> sourcePlan,
        Map<String, Object> session, List<Map<String, Object>> comparisonCandidates) {
}
