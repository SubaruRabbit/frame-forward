package com.frameforward.evaluation.model.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoEvaluationRequest {

	public String mediaId, intent, sessionId;

	public boolean reanalyze;

	public Map<String, Object> mockOutput;

}
