package com.frameforward.evaluation.service;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.evaluation.manager.EvaluationManager;
import com.frameforward.evaluation.model.entity.*;
class PortfolioEvaluationBoundaryTest {
    @Test
    void missingEvaluationProducesOriginalEmptyWorkflowContext() {
        var query = new PortfolioEvaluationQuery(mock(EvaluationManager.class), new ObjectMapper());
        var result = query.findOwned("owner", "media");
        assertThat(result.evaluation()).isNull();
        assertThat(result.sourcePlan()).isNull();
        assertThat(result.retake()).isNull();
        assertThat(result.workflowContext().mediaId()).isEqualTo("media");
        assertThat(result.workflowContext().comparisonCandidates()).isEmpty();
    }
    @Test
    void preservesNullableResultRetakeProjectionAndDiagnosticJsonFailure() {
        var manager = mock(EvaluationManager.class);
        var query = new PortfolioEvaluationQuery(manager, new ObjectMapper());
        var evaluation = new PhotoEvaluationEntity();
        evaluation.id = "eval";
        evaluation.accountId = "owner";
        when(manager.latestOwned("owner", "media")).thenReturn(evaluation);
        assertThat(query.findOwned("owner", "media").evaluation()).isNull();
        evaluation.resultJson = " ";
        assertThat(query.findOwned("owner", "media").workflowContext().session()).isNull();
        var retake = new RetakeLinkEntity();
        retake.originalEvaluationId = "original";
        retake.retakeEvaluationId = "retake";
        when(manager.relatedRetake("owner", "eval")).thenReturn(retake);
        evaluation.resultJson = "{\"total\":80}";
        assertThat(query.findOwned("owner", "media").retake())
                .isEqualTo(Map.of("originalEvaluationId", "original", "retakeEvaluationId", "retake"));
        evaluation.sessionId = "missing";
        assertThat(query.findOwned("owner", "media").sourcePlan()).isNull();
        evaluation.resultJson = "invalid";
        assertThatThrownBy(() -> query.findOwned("owner", "media")).isInstanceOf(IllegalStateException.class)
                .hasMessage("评测结果无法读取").hasCauseInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class);
    }
}
