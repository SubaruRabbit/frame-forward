package com.frameforward.evaluation.model.dto;
import java.util.Map;
public class PhotoEvaluationRequest {
    public String mediaId, intent, sessionId;
    public boolean reanalyze;
    public Map<String, Object> mockOutput;
}
