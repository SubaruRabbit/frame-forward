package com.frameforward.ai.business;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

/** 仅根据前后主要问题的证据归因改善，分数变化不能单独作为改善证据。 */
@Component
public class RetakeComparisonGraph {
    public Map<String, Object> summarize(Map<String, Object> original, Map<String, Object> retake) {
        Set<String> originalProblems = primaryProblems(original);
        Set<String> retakeProblems = primaryProblems(retake);
        List<String> improved = originalProblems.stream().filter(problem -> !retakeProblems.contains(problem)).toList();
        List<String> remaining = originalProblems.stream().filter(retakeProblems::contains).toList();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("improvedProblems", improved);
        summary.put("remainingProblems", remaining);
        summary.put("nextPracticeAdvice", remaining.isEmpty() ? "保持本次有效调整，并在相同目标下继续稳定复现。" : "下一次优先针对仍存在的主要问题练习。");
        return summary;
    }

    private Set<String> primaryProblems(Map<String, Object> evaluation) {
        Object raw = evaluation == null ? null : evaluation.get("primaryProblems");
        if (!(raw instanceof List<?> values))
            return Set.of();
        Set<String> problems = new LinkedHashSet<>();
        for (Object value : values)
            if (value instanceof String problem && !problem.isBlank())
                problems.add(problem);
        return problems;
    }
}
