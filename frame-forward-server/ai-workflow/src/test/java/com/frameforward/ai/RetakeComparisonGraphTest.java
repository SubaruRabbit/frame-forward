package com.frameforward.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class RetakeComparisonGraphTest {
    private final RetakeComparisonGraph graph = new RetakeComparisonGraph();

    @Test
    void doesNotMarkAnUnfixedPrimaryProblemImprovedOnlyBecauseScoreRises() {
        Map<String, Object> comparison = graph.summarize(Map.of("total", 60, "primaryProblems", List.of("背景杂乱")),
                Map.of("total", 82, "primaryProblems", List.of("背景杂乱")));

        assertTrue(((List<?>) comparison.get("improvedProblems")).isEmpty());
        assertEquals(List.of("背景杂乱"), comparison.get("remainingProblems"));
        assertFalse(comparison.containsKey("scoreOnlyImprovement"));
    }

    @Test
    void marksAResolvedPrimaryProblemAsImprovedWithEvidence() {
        Map<String, Object> comparison = graph.summarize(
                Map.of("total", 60, "primaryProblems", List.of("背景杂乱", "主体欠曝")),
                Map.of("total", 82, "primaryProblems", List.of("主体欠曝")));

        assertEquals(List.of("背景杂乱"), comparison.get("improvedProblems"));
        assertEquals(List.of("主体欠曝"), comparison.get("remainingProblems"));
        assertTrue(comparison.get("nextPracticeAdvice") instanceof String);
    }
}
