package com.frameforward.shooting.model.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShootingPlanRequest {

	public String sceneAnalysisId;

	public Map<String, Object> mockOutput;

}
