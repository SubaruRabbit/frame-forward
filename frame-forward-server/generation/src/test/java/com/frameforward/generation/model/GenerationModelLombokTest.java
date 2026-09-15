package com.frameforward.generation.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.frameforward.generation.model.dto.ReferenceImageRequest;
import com.frameforward.generation.model.entity.ReferenceImageEntity;

class GenerationModelLombokTest {

	@Test
	void buildsReferenceRequestsWithoutChangingNestedMaps() {
		var selectedPlan = Map.<String, Object>of("label", "A", "position", "front");
		var request = ReferenceImageRequest.builder().environmentMediaId("media").shootingPlanId("plan")
				.selectedPlan(selectedPlan).mockOutput(Map.of("imageUrl", "mock")).build();

		assertThat(request.environmentMediaId).isEqualTo("media");
		assertThat(request.shootingPlanId).isEqualTo("plan");
		assertThat(request.selectedPlan).isSameAs(selectedPlan);
	}

	@Test
	void buildsReferenceEntityAndKeepsCallbackLookupAccessor() {
		var entity = ReferenceImageEntity.builder().id("reference").accountId("account").aiTaskId("task")
				.generatedMediaId("media").build();

		assertThat(entity.getAiTaskId()).isEqualTo("task");
		assertThat(entity.generatedMediaId).isEqualTo("media");
	}

}
