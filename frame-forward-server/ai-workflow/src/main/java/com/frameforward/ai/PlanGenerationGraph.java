package com.frameforward.ai;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class PlanGenerationGraph {
    @SuppressWarnings("unchecked")
    public Map<String, Object> execute(Map<String, Object> input) {
        Object output = input.get("mockOutput");
        return output instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }
    @SuppressWarnings("unchecked")
    public boolean valid(Map<String, Object> output) {
        if (output == null || !(output.get("plans") instanceof List<?> plans) || plans.size() < 2 || plans.size() > 3)
            return false;
        long recommended = plans.stream().filter(Map.class::isInstance).map(Map.class::cast)
                .filter(plan -> Boolean.TRUE.equals(plan.get("recommended"))).count();
        return recommended == 1 && plans.stream().allMatch(this::completeAndSafe);
    }
    private boolean completeAndSafe(Object value) {
        if (!(value instanceof Map<?, ?> plan))
            return false;
        String position = String.valueOf(plan.get("position"));
        if (position.contains("ROADWAY") || position.contains("车道") || position.contains("边缘"))
            return false;
        if (!(plan.get("focalLengthMm") instanceof Number focalLength) || focalLength.intValue() < 1
                || focalLength.intValue() > 1200)
            return false;
        if (!(plan.get("accessoryUse") instanceof String accessory) || !(plan.get("focus") instanceof String focus)
                || accessory.isBlank() || focus.isBlank())
            return false;
        Object exposure = plan.get("exposure");
        if (!(exposure instanceof Map<?, ?> settings) || !Boolean.TRUE.equals(settings.get("startingPoint"))
                || !(settings.get("iso") instanceof Number iso) || iso.intValue() < 50 || iso.intValue() > 102400
                || blank(settings.get("aperture")) || blank(settings.get("shutterSpeed")))
            return false;
        return plan.get("steps") instanceof List<?> steps && !steps.isEmpty();
    }
    private static boolean blank(Object value) {
        return !(value instanceof String text) || text.isBlank();
    }
}
