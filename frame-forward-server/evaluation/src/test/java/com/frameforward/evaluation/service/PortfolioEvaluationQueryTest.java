package com.frameforward.evaluation.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.evaluation.mapper.PhotoEvaluationMapper;
import com.frameforward.evaluation.mapper.RetakeLinkMapper;
import com.frameforward.evaluation.mapper.ShootingSessionMapper;
import com.frameforward.evaluation.model.dto.PortfolioEvaluationDetail;
import com.frameforward.evaluation.model.entity.PhotoEvaluationEntity;
import com.frameforward.evaluation.model.entity.ShootingSessionEntity;

class PortfolioEvaluationQueryTest {
    @Test
    void returnsOnlyCompletedCandidatesForTheOwnedSession() {
        PhotoEvaluationMapper evaluations = mock(PhotoEvaluationMapper.class);
        ShootingSessionMapper sessions = mock(ShootingSessionMapper.class);
        RetakeLinkMapper retakes = mock(RetakeLinkMapper.class);
        PhotoEvaluationEntity current = evaluation("evaluation-1", "media-1", "session-1", "{\"total\":80}");
        PhotoEvaluationEntity candidate = evaluation("evaluation-2", "media-2", "session-1", "{\"total\":90}");
        ShootingSessionEntity session = new ShootingSessionEntity();
        session.id = "session-1";
        session.accountId = "account-1";
        session.shootingPlanId = "plan-1";
        session.planContext = "保持三分构图";
        when(evaluations.selectOne(any())).thenReturn(current);
        when(evaluations.selectList(any())).thenReturn(List.of(current, candidate));
        when(sessions.selectOne(any())).thenReturn(session);

        PortfolioEvaluationDetail detail = new PortfolioEvaluationQuery(
                new com.frameforward.evaluation.manager.EvaluationManager(
                        new com.frameforward.evaluation.repository.EvaluationRepository(evaluations, sessions,
                                retakes)),
                new ObjectMapper()).findOwned("account-1", "media-1");

        assertEquals("media-1", detail.workflowContext().mediaId());
        assertEquals("evaluation-1", detail.workflowContext().evaluation().get("evaluationId"));
        assertEquals("session-1", detail.workflowContext().session().get("sessionId"));
        assertEquals(2, detail.workflowContext().comparisonCandidates().size());
    }

    private static PhotoEvaluationEntity evaluation(String id, String mediaId, String sessionId, String resultJson) {
        PhotoEvaluationEntity evaluation = new PhotoEvaluationEntity();
        evaluation.id = id;
        evaluation.accountId = "account-1";
        evaluation.mediaId = mediaId;
        evaluation.sessionId = sessionId;
        evaluation.resultJson = resultJson;
        evaluation.createdAt = Instant.now();
        return evaluation;
    }
}
