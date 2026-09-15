package com.frameforward.generation.model.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceImageRequest {

	public String environmentMediaId, shootingPlanId;

	public Map<String, Object> selectedPlan, mockOutput;

}
