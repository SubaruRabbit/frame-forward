package com.frameforward.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.frameforward.evaluation.service.RetakeComparisonService;

class AiRetakeRuleMigrationTest {
    @Test
    void comparisonUsesCanonicalBusinessRule() throws Exception {
        assertThat(RetakeComparisonService.class.getDeclaredField("graph").getType().getName())
                .isEqualTo("com.frameforward.ai.business.RetakeComparisonGraph");
    }
}
