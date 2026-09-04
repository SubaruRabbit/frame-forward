package com.frameforward.evaluation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 为作品集提供评测、拍摄方案关联和重拍关系的只读投影。 */
@Component
public class PortfolioEvaluationQuery {
    private final PhotoEvaluationMapper evaluations;
    private final ShootingSessionMapper sessions;
    private final RetakeLinkMapper retakes;
    private final ObjectMapper json;

    public PortfolioEvaluationQuery(PhotoEvaluationMapper evaluations, ShootingSessionMapper sessions,
            RetakeLinkMapper retakes, ObjectMapper json) {
        this.evaluations = evaluations;
        this.sessions = sessions;
        this.retakes = retakes;
        this.json = json;
    }

    public Detail findOwned(String accountId, String mediaId) {
        PhotoEvaluationEntity evaluation = evaluations.selectOne(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                .eq(PhotoEvaluationEntity::getAccountId, accountId).eq(PhotoEvaluationEntity::getMediaId, mediaId)
                .orderByDesc(PhotoEvaluationEntity::getCreatedAt).last("LIMIT 1"));
        if (evaluation == null)
            return Detail.empty(mediaId);
        Map<String, Object> result = read(evaluation.resultJson);
        ShootingSessionEntity session = evaluation.sessionId == null
                ? null
                : sessions.selectOne(new LambdaQueryWrapper<ShootingSessionEntity>()
                        .eq(ShootingSessionEntity::getId, evaluation.sessionId)
                        .eq(ShootingSessionEntity::getAccountId, accountId));
        Map<String, Object> plan = session == null
                ? null
                : Map.of("shootingPlanId", session.shootingPlanId, "planContext", session.planContext);
        RetakeLinkEntity retake = retakes
                .selectOne(new LambdaQueryWrapper<RetakeLinkEntity>().eq(RetakeLinkEntity::getAccountId, accountId)
                        .and(query -> query.eq(RetakeLinkEntity::getRetakeEvaluationId, evaluation.id).or()
                                .eq(RetakeLinkEntity::getOriginalEvaluationId, evaluation.id)));
        Map<String, Object> retakeView = retake == null
                ? null
                : Map.of("originalEvaluationId", retake.originalEvaluationId, "retakeEvaluationId",
                        retake.retakeEvaluationId);
        return new Detail(result, plan, retakeView, workflow(mediaId, evaluation, session));
    }

    private WorkflowContext workflow(String mediaId, PhotoEvaluationEntity evaluation, ShootingSessionEntity session) {
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
                : evaluations
                        .selectList(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                                .eq(PhotoEvaluationEntity::getAccountId, evaluation.accountId)
                                .eq(PhotoEvaluationEntity::getSessionId, session.id)
                                .isNotNull(PhotoEvaluationEntity::getResultJson))
                        .stream().map(item -> Map.<String, Object>of("evaluationId", item.id, "mediaId", item.mediaId))
                        .toList();
        return new WorkflowContext(mediaId, evaluationView, plan, sessionView, candidates);
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

    public record Detail(Map<String, Object> evaluation, Map<String, Object> sourcePlan, Map<String, Object> retake,
            WorkflowContext workflowContext) {
        static Detail empty(String mediaId) {
            return new Detail(null, null, null, new WorkflowContext(mediaId, null, null, null, List.of()));
        }
    }
    public record WorkflowContext(String mediaId, Map<String, Object> evaluation, Map<String, Object> sourcePlan,
            Map<String, Object> session, List<Map<String, Object>> comparisonCandidates) {
    }
}
