package com.frameforward.ai.business;
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
        addCalculatedTotal(output, input.get("weights"));
        addExifLimit(output, input);
        return output;
    }
    private static void addCalculatedTotal(Map<String, Object> output, Object suppliedWeights) {
        if (output.containsKey("total") || !(output.get("dimensions") instanceof Map<?, ?> scores))
            return;
        var average = weightedAverage(scores, suppliedWeights);
        if (average != null)
            output.put("total", Math.round(average));
    }
    private static Double weightedAverage(Map<?, ?> scores, Object suppliedWeights) {
        double weighted = 0, weights = 0;
        for (var entry : scores.entrySet()) {
            if (!(entry.getValue() instanceof Number score))
                continue;
            var weight = weightFor(entry.getKey(), suppliedWeights);
            weighted += score.doubleValue() * weight;
            weights += weight;
        }
        return weights > 0 ? weighted / weights : null;
    }
    private static double weightFor(Object dimension, Object suppliedWeights) {
        if (suppliedWeights instanceof Map<?, ?> weights
                && weights.get(String.valueOf(dimension)) instanceof Number weight)
            return weight.doubleValue();
        return 1;
    }
    private static void addExifLimit(Map<String, Object> output, Map<String, Object> input) {
        if (Boolean.FALSE.equals(input.get("hasExif")))
            output.put("exifLimit", "缺少可用 EXIF；参数诊断仅基于画面观察。");
    }
    public boolean valid(Map<String, Object> value) {
        return hasRequiredFields(value) && hasValidDimensions(value) && hasSupportedDiagnosis(value);
    }
    private static boolean hasRequiredFields(Map<String, Object> value) {
        return value != null && value.get("total") instanceof Number total && total.intValue() >= 1
                && total.intValue() <= 100 && value.get("dimensions") instanceof Map<?, ?> dimensions
                && !dimensions.isEmpty() && value.get("strengths") instanceof List<?>
                && value.get("primaryProblems") instanceof List<?> && value.get("priorityImprovement") instanceof String
                && value.get("retakeSteps") instanceof List<?>;
    }
    private static boolean hasValidDimensions(Map<String, Object> value) {
        for (Object score : ((Map<?, ?>) value.get("dimensions")).values())
            if (!(score instanceof Number number) || number.intValue() < 1 || number.intValue() > 100)
                return false;
        return true;
    }
    private static boolean hasSupportedDiagnosis(Map<String, Object> value) {
        Object diagnosis = value.get("technicalDiagnosis");
        return diagnosis instanceof Map<?, ?> map
                && ("OBSERVATION".equals(map.get("certainty")) || "INFERENCE".equals(map.get("certainty")));
    }
}
