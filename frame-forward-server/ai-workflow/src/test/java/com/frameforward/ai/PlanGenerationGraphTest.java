package com.frameforward.ai;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class PlanGenerationGraphTest {
    private final PlanGenerationGraph graph = new PlanGenerationGraph();
    @Test
    void rejectsUnsafeOrInvalidExposurePlans() {
        assertFalse(graph.valid(Map.of("plans", List.of(plan("车道中央", 400), plan("人行道", 400)))));
        assertFalse(graph.valid(Map.of("plans", List.of(plan("人行道", 25), plan("人行道", 400)))));
        assertTrue(graph.valid(Map.of("plans", List.of(plan("人行道", 400), plan("广场", 800)))));
    }
    private Map<String, Object> plan(String position, int iso) {
        return Map.of("recommended", position.equals("人行道"), "position", position, "focalLengthMm", 35, "accessoryUse",
                "无需附件", "focus", "单次自动对焦", "exposure",
                Map.of("aperture", "f/2.8", "shutterSpeed", "1/250s", "iso", iso, "startingPoint", true), "steps",
                List.of("试拍"));
    }
}
