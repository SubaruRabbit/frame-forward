package com.frameforward.evaluation.service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.evaluation.manager.EvaluationManager;
import com.frameforward.evaluation.model.dto.PortfolioEvaluationDetail;
import com.frameforward.evaluation.model.dto.PortfolioWorkflowContext;
import com.frameforward.evaluation.model.entity.PhotoEvaluationEntity;
import com.frameforward.evaluation.model.entity.RetakeLinkEntity;
import com.frameforward.evaluation.model.entity.ShootingSessionEntity;

/** 为作品集提供评测、拍摄方案关联和重拍关系的只读投影。 */
@Component
public class PortfolioEvaluationQuery {
    private final EvaluationManager manager;
    private final ObjectMapper json;

    public PortfolioEvaluationQuery(EvaluationManager manager, ObjectMapper json) {
        this.manager = manager;
        this.json = json;
    }

    public PortfolioEvaluationDetail findOwned(String accountId, String mediaId) {
        PhotoEvaluationEntity evaluation = manager.latestOwned(accountId, mediaId);
        if (evaluation == null)
            return new PortfolioEvaluationDetail(null, null, null,
                    new PortfolioWorkflowContext(mediaId, null, null, null, List.of()));
        Map<String, Object> result = read(evaluation.resultJson);
        ShootingSessionEntity session = evaluation.sessionId == null
                ? null
                : manager.findOwnedSession(accountId, evaluation.sessionId);
        Map<String, Object> plan = session == null
                ? null
                : Map.of("shootingPlanId", session.shootingPlanId, "planContext", session.planContext);
        RetakeLinkEntity retake = manager.relatedRetake(accountId, evaluation.id);
        Map<String, Object> retakeView = retake == null
                ? null
                : Map.of("originalEvaluationId", retake.originalEvaluationId, "retakeEvaluationId",
                        retake.retakeEvaluationId);
        return new PortfolioEvaluationDetail(result, plan, retakeView, workflow(mediaId, evaluation, session));
    }

    private PortfolioWorkflowContext workflow(String mediaId, PhotoEvaluationEntity evaluation,
            ShootingSessionEntity session) {
        Map<String, Object> evaluationView = new LinkedHashMap<>();
        evaluationView.put("evaluationId", evaluation.id);
        evaluationView.put("sessionId", evaluation.sessionId);
        Map<String, Object> plan = session == null
                ? null
                : Map.of("shootingPlanId", session.shootingPlanId, "planContext", session.planContext);
        Map<String, Object> sessionView = session == null
                ? null
                : Map.of("sessionId", session.id, "shootingPlanId", session.shootingPlanId, "planContext",
                        session.planContext);
        List<Map<String, Object>> candidates = session == null
                ? List.of()
                : manager.completedCandidates(evaluation.accountId, session.id).stream()
                        .map(item -> Map.<String, Object>of("evaluationId", item.id, "mediaId", item.mediaId)).toList();
        return new PortfolioWorkflowContext(mediaId, evaluationView, plan, sessionView, candidates);
    }

    private Map<String, Object> read(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return json.readValue(value, new TypeReference<LinkedHashMap<String, Object>>() {
            });
        } catch (Exception exception) {
            throw new IllegalStateException("评测结果无法读取", exception);
        }
    }

}
