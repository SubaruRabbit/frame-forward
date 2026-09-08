package com.frameforward.ai.business;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class SceneAnalysisGraphTest {
    private final SceneAnalysisGraph graph = new SceneAnalysisGraph();

    @Test
    void removesRoadwayAndEdgeWhilePreservingSafePositions() {
        Map<String, Object> supplied = output();
        var safe = Map.of("zone", "SAFE", "position", "广场");
        supplied.put("usablePositions", List.of("invalid", Map.of("zone", "ROADWAY"), Map.of("zone", "EDGE"), safe));
        Map<String, Object> result = graph.execute(Map.of("mockOutput", supplied));
        assertThat((List<?>) result.get("usablePositions")).isEqualTo(List.of(safe));
        assertThat((List<?>) result.get("safetyWarnings")).hasSize(2);
        assertThat(graph.valid(result)).isTrue();
        assertThat((List<?>) supplied.get("usablePositions")).hasSize(4);
    }

    @Test
    void missingOrMalformedOutputAndPositionsAreHandled() {
        assertThat(graph.execute(Map.of())).isEmpty();
        assertThat(graph.execute(Map.of("mockOutput", "invalid"))).isEmpty();
        var result = graph.execute(Map.of("mockOutput", Map.of("usablePositions", "invalid")));
        assertThat(result.get("usablePositions")).isEqualTo(List.of());
        assertThat(result.get("safetyWarnings")).isEqualTo(List.of());
    }

    @Test
    void validatesEveryRequiredFieldAndNonblankSceneType() {
        assertThat(graph.valid(null)).isFalse();
        assertThat(graph.valid(output())).isTrue();
        for (String key : output().keySet()) {
            var missing = output();
            missing.remove(key);
            assertThat(graph.valid(missing)).as(key).isFalse();
        }
        var blank = output();
        blank.put("sceneType", " ");
        assertThat(graph.valid(blank)).isFalse();
    }

    private static Map<String, Object> output() {
        return new HashMap<>(Map.of("sceneType", "portrait", "subjectCandidates", List.of(), "light", Map.of(),
                "backgroundComplexity", "simple", "compositionalStructures", List.of(), "usablePositions", List.of(),
                "accessoryOpportunities", List.of(), "safetyWarnings", List.of()));
    }
}
