package com.frameforward.shooting.model.dto;
import java.util.List;
import java.util.Map;
public class SceneAnalysisRequest {
    public String environmentMediaId;
    public String subjectType;
    public String subject;
    public String targetStyle;
    public Integer timeConstraintMinutes;
    public List<String> equipmentIds;
    public Map<String, Object> mockOutput;
}
