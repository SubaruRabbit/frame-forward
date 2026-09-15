package com.frameforward.ai.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.entity.AiTaskEntity;

class AiModelLombokTest {

	@Test
	void buildsMutableCreateRequestsWithoutChangingPropertySemantics() {
		var request = AiTaskCreateRequest.builder().operationType("coach").input(Map.of("question", "composition"))
				.build();

		assertThat(request.operationType).isEqualTo("coach");
		assertThat(request.input).containsEntry("question", "composition");
		assertThat(new AiTaskCreateRequest()).isNotNull();
	}

	@Test
	void buildsTaskEntitiesAndKeepsMybatisAccessors() {
		var now = Instant.parse("2026-01-01T00:00:00Z");
		var entity = AiTaskEntity.builder().id("task").accountId("account").operationType("coach").idempotencyKey("key")
				.state("QUEUED").inputJson("{}").createdAt(now).updatedAt(now).build();

		assertThat(entity.getId()).isEqualTo("task");
		assertThat(entity.getAccountId()).isEqualTo("account");
		assertThat(entity.getOperationType()).isEqualTo("coach");
		assertThat(entity.getIdempotencyKey()).isEqualTo("key");
		assertThat(entity.getState()).isEqualTo("QUEUED");
	}

}
