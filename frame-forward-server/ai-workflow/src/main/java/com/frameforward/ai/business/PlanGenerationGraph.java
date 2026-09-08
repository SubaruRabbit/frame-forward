package com.frameforward.ai.business;

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
        return hasSafePosition(plan) && hasSupportedFocalLength(plan) && hasRequiredInstructions(plan)
                && hasSafeExposure(plan) && hasSteps(plan);
    }
    private static boolean hasSafePosition(Map<?, ?> plan) {
        String position = String.valueOf(plan.get("position"));
        return !position.contains("ROADWAY") && !position.contains("车道") && !position.contains("边缘");
    }
    private static boolean hasSupportedFocalLength(Map<?, ?> plan) {
        return plan.get("focalLengthMm") instanceof Number focalLength && focalLength.intValue() >= 1
                && focalLength.intValue() <= 1200;
    }
    private static boolean hasRequiredInstructions(Map<?, ?> plan) {
        return plan.get("accessoryUse") instanceof String accessory && !accessory.isBlank()
                && plan.get("focus") instanceof String focus && !focus.isBlank();
    }
    private static boolean hasSafeExposure(Map<?, ?> plan) {
        if (!(plan.get("exposure") instanceof Map<?, ?> settings))
            return false;
        return Boolean.TRUE.equals(settings.get("startingPoint")) && hasSupportedIso(settings)
                && !blank(settings.get("aperture")) && !blank(settings.get("shutterSpeed"));
    }
    private static boolean hasSupportedIso(Map<?, ?> settings) {
        return settings.get("iso") instanceof Number iso && iso.intValue() >= 50 && iso.intValue() <= 102400;
    }
    private static boolean hasSteps(Map<?, ?> plan) {
        return plan.get("steps") instanceof List<?> steps && !steps.isEmpty();
    }
    private static boolean blank(Object value) {
        return !(value instanceof String text) || text.isBlank();
    }
}
