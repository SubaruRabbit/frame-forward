package com.frameforward.ai.business;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;

class PhotoEvaluationGraphTest {
    private final PhotoEvaluationGraph graph = new PhotoEvaluationGraph();
    @Test
    void producesValidVisualOnlyAndExifAwareOutput() {
        var visual = graph.execute(Map.of("hasExif", false, "mockOutput", output()));
        assertTrue(graph.valid(visual));
        assertTrue(visual.containsKey("exifLimit"));
        var exif = graph.execute(Map.of("hasExif", true, "mockOutput", output()));
        assertTrue(graph.valid(exif));
        assertFalse(exif.containsKey("exifLimit"));
    }
    @Test
    void rejectsAbsoluteOrOutOfRangeResult() {
        var invalid = new HashMap<>(output());
        invalid.put("total", 101);
        assertFalse(graph.valid(invalid));
    }
    @Test
    void calculatesWeightedTotalAndRejectsInvalidDimensionScore() {
        var weighted = graph.execute(Map.of("mockOutput", output(), "weights", Map.of("composition", 3, "light", 1)));
        assertEquals(78L, weighted.get("total"));
        var invalid = new HashMap<>(output());
        invalid.put("total", 50);
        invalid.put("dimensions", Map.of("composition", 0));
        assertFalse(graph.valid(invalid));
    }
    private Map<String, Object> output() {
        return Map.of("dimensions", Map.of("composition", 80, "light", 70), "strengths", List.of("清晰"),
                "primaryProblems", List.of("背景杂"), "priorityImprovement", "整理背景", "technicalDiagnosis",
                Map.of("certainty", "INFERENCE", "text", "可能由运动或手持导致"), "retakeSteps", List.of("固定机位"));
    }
}
