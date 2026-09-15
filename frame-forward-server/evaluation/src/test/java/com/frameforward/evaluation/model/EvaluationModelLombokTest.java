package com.frameforward.evaluation.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.frameforward.evaluation.model.dto.PhotoEvaluationRequest;
import com.frameforward.evaluation.model.dto.RetakeComparisonRequest;
import com.frameforward.evaluation.model.dto.ShootingSessionRequest;
import com.frameforward.evaluation.model.entity.PhotoEvaluationEntity;
import com.frameforward.evaluation.model.entity.RetakeLinkEntity;
import com.frameforward.evaluation.model.entity.ShootingSessionEntity;

class EvaluationModelLombokTest {

	@Test
	void buildsMutableRequestsWithOriginalDefaults() {
		var evaluation = PhotoEvaluationRequest.builder().mediaId("media").intent("portrait").sessionId("session")
				.reanalyze(true).mockOutput(Map.of("score", 80)).build();
		var retake = RetakeComparisonRequest.builder().originalEvaluationId("original").retakeEvaluationId("retake")
				.build();
		var session = ShootingSessionRequest.builder().shootingPlanId("plan").planContext("context").build();

		assertThat(evaluation.reanalyze).isTrue();
		assertThat(retake.originalEvaluationId).isEqualTo("original");
		assertThat(session.shootingPlanId).isEqualTo("plan");
		assertThat(new PhotoEvaluationRequest().reanalyze).isFalse();
	}

	@Test
	void buildsEntitiesAndKeepsMybatisAccessors() {
		var evaluation = PhotoEvaluationEntity.builder().id("evaluation").accountId("account").mediaId("media")
				.resultJson("{}").build();
		var retake = RetakeLinkEntity.builder().retakeEvaluationId("retake").originalEvaluationId("original")
				.accountId("account").build();
		var session = ShootingSessionEntity.builder().id("session").accountId("account").shootingPlanId("plan").build();

		assertThat(evaluation.getResultJson()).isEqualTo("{}");
		assertThat(retake.getOriginalEvaluationId()).isEqualTo("original");
		assertThat(session.getShootingPlanId()).isEqualTo("plan");
	}

}
