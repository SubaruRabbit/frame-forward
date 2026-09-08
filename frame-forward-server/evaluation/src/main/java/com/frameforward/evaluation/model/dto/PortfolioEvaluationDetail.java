package com.frameforward.evaluation.model.dto;
import java.util.Map;
public record PortfolioEvaluationDetail(Map<String, Object> evaluation, Map<String, Object> sourcePlan,
        Map<String, Object> retake, PortfolioWorkflowContext workflowContext) {
}
