package com.frameforward.ai.business;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class PlanGenerationBoundaryTest {
    private final PlanGenerationGraph graph = new PlanGenerationGraph();

    @Test
    void requiresStructuredOutputAndTwoOrThreePlansWithOneRecommendation() {
        assertThat(graph.execute(Map.of())).isEmpty();
        var output = Map.<String, Object>of("plans", List.of(plan(true), plan(false)));
        assertThat(graph.execute(Map.of("mockOutput", output))).isSameAs(output);
        assertThat(graph.valid(output)).isTrue();
        assertThat(graph.valid(null)).isFalse();
        assertThat(graph.valid(Map.of())).isFalse();
        assertThat(graph.valid(Map.of("plans", List.of(plan(true))))).isFalse();
        assertThat(graph.valid(Map.of("plans", List.of(plan(true), plan(false), plan(false), plan(false))))).isFalse();
        assertThat(graph.valid(Map.of("plans", List.of(plan(true), "invalid")))).isFalse();
        assertThat(graph.valid(Map.of("plans", List.of(plan(false), plan(false))))).isFalse();
    }

    @Test
    void rejectsUnsafeOrIncompletePlanFields() {
        for (String position : List.of("ROADWAY", "车道", "边缘"))
            rejects("position", position);
        for (Object focal : List.of("invalid", 0, 1201))
            rejects("focalLengthMm", focal);
        for (Object text : List.of(1, " ")) {
            rejects("accessoryUse", text);
            rejects("focus", text);
        }
        rejects("steps", "invalid");
        rejects("steps", List.of());
        rejects("exposure", "invalid");
    }

    @Test
    void rejectsUnsafeAndIncompleteExposure() {
        for (Object iso : List.of("invalid", 49, 102401))
            rejectsExposure("iso", iso);
        rejectsExposure("startingPoint", false);
        for (Object text : List.of(1, " ")) {
            rejectsExposure("aperture", text);
            rejectsExposure("shutterSpeed", text);
        }
    }

    private void rejects(String key, Object value) {
        var invalid = plan(true);
        invalid.put(key, value);
        assertThat(graph.valid(Map.of("plans", List.of(invalid, plan(false))))).as(key + "=" + value).isFalse();
    }

    private void rejectsExposure(String key, Object value) {
        var exposure = exposure();
        exposure.put(key, value);
        rejects("exposure", exposure);
    }

    private static Map<String, Object> plan(boolean recommended) {
        return new HashMap<>(Map.of("recommended", recommended, "position", "SAFE", "focalLengthMm", 50, "accessoryUse",
                "三脚架", "focus", "单点", "exposure", exposure(), "steps", List.of("构图")));
    }

    private static Map<String, Object> exposure() {
        return new HashMap<>(Map.of("startingPoint", true, "iso", 100, "aperture", "f/4", "shutterSpeed", "1/125"));
    }
}
