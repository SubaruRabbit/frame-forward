package com.frameforward.shooting.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.frameforward.shooting.model.dto.SceneAnalysisRequest;
import com.frameforward.shooting.model.dto.ShootingPlanRequest;
import com.frameforward.shooting.model.entity.SceneAnalysisEntity;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;

class ShootingModelLombokTest {

	@Test
	void requestBuildersKeepMutableJsonProperties() {
		var scene = SceneAnalysisRequest.builder().environmentMediaId("media").subjectType("PERSON").subject("runner")
				.targetStyle("cinematic").timeConstraintMinutes(30).equipmentIds(List.of("camera"))
				.mockOutput(Map.of("result", "ok")).build();
		var plan = ShootingPlanRequest.builder().sceneAnalysisId("scene").mockOutput(Map.of("plans", List.of()))
				.build();

		assertThat(scene.environmentMediaId).isEqualTo("media");
		assertThat(scene.equipmentIds).containsExactly("camera");
		assertThat(plan.sceneAnalysisId).isEqualTo("scene");
	}

	@Test
	void entityBuildersKeepMybatisAccessors() {
		var scene = SceneAnalysisEntity.builder().id("scene").accountId("account").aiTaskId("task").build();
		var plan = ShootingPlanEntity.builder().id("plan").accountId("account").sceneAnalysisId("scene")
				.aiTaskId("task").build();

		assertThat(scene.getId()).isEqualTo("scene");
		assertThat(scene.getAccountId()).isEqualTo("account");
		assertThat(scene.getAiTaskId()).isEqualTo("task");
		assertThat(plan.getId()).isEqualTo("plan");
		assertThat(plan.getAccountId()).isEqualTo("account");
		assertThat(plan.getAiTaskId()).isEqualTo("task");
	}

}
