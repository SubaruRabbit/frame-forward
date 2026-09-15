package com.frameforward.evaluation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetakeComparisonRequest {

	public String originalEvaluationId, retakeEvaluationId;

}
