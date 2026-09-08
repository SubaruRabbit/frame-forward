package com.frameforward.ai.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class RetakeComparisonGraphTest {
    private final RetakeComparisonGraph graph = new RetakeComparisonGraph();

    @Test
    void handlesMissingMalformedAndResolvedProblemsWithoutInventingEvidence() {
        for (Map<String, Object> malformed : List.of(Map.<String, Object>of(),
                Map.<String, Object>of("primaryProblems", "invalid"))) {
            var result = graph.summarize(malformed, null);
            assertEquals(List.of(), result.get("improvedProblems"));
            assertEquals(List.of(), result.get("remainingProblems"));
        }
        assertEquals(List.of(), graph.summarize(null, null).get("remainingProblems"));
        var resolved = graph.summarize(Map.of("primaryProblems", List.of("背景杂乱", " ", 1, "背景杂乱")), Map.of());
        assertEquals(List.of("背景杂乱"), resolved.get("improvedProblems"));
        assertEquals(List.of(), resolved.get("remainingProblems"));
        assertEquals("保持本次有效调整，并在相同目标下继续稳定复现。", resolved.get("nextPracticeAdvice"));
    }

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
