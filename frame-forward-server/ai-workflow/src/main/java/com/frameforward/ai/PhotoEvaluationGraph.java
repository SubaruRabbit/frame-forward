package com.frameforward.ai;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
@Component
public class PhotoEvaluationGraph {
    @SuppressWarnings("unchecked")
    public Map<String, Object> execute(Map<String, Object> input) {
        Object supplied = input.get("mockOutput");
        if (!(supplied instanceof Map<?, ?> raw))
            return Map.of();
        var output = new LinkedHashMap<>((Map<String, Object>) raw);
        Object dimensions = output.get("dimensions");
        if (dimensions instanceof Map<?, ?> scores && !output.containsKey("total")) {
            double weighted = 0, weights = 0;
            Object suppliedWeights = input.get("weights");
            for (var entry : scores.entrySet())
                if (entry.getValue() instanceof Number score) {
                    double weight = suppliedWeights instanceof Map<?, ?> map
                            && map.get(String.valueOf(entry.getKey())) instanceof Number value
                                    ? value.doubleValue()
                                    : 1;
                    weighted += score.doubleValue() * weight;
                    weights += weight;
                }
            if (weights > 0)
                output.put("total", Math.round(weighted / weights));
        }
        if (Boolean.FALSE.equals(input.get("hasExif")))
            output.put("exifLimit", "缺少可用 EXIF；参数诊断仅基于画面观察。");
        return output;
    }
    public boolean valid(Map<String, Object> value) {
        if (value == null || !(value.get("total") instanceof Number total) || total.intValue() < 1
                || total.intValue() > 100 || !(value.get("dimensions") instanceof Map<?, ?> dimensions)
                || dimensions.isEmpty() || !(value.get("strengths") instanceof List<?>)
                || !(value.get("primaryProblems") instanceof List<?>)
                || !(value.get("priorityImprovement") instanceof String)
                || !(value.get("retakeSteps") instanceof List<?>))
            return false;
        for (Object score : dimensions.values())
            if (!(score instanceof Number number) || number.intValue() < 1 || number.intValue() > 100)
                return false;
        Object diagnosis = value.get("technicalDiagnosis");
        return diagnosis instanceof Map<?, ?> map
                && ("OBSERVATION".equals(map.get("certainty")) || "INFERENCE".equals(map.get("certainty")));
    }
}
