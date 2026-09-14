package com.frameforward.generation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class AiTaskConsumerMigrationTest {

	@Test
	void serviceUsesCanonicalRuntimeAndCreatedData() throws Exception {
		Class<?> service = Class.forName("com.frameforward.generation.service.ReferenceImageService");
		assertThat(service.getDeclaredField("tasks").getType().getName())
				.isEqualTo("com.frameforward.ai.service.AiTaskRuntime");
		assertThat(Arrays.stream(service.getDeclaredMethods()).filter(method -> method.getName().equals("create"))
				.map(method -> method.getReturnType().getName()))
				.contains("com.frameforward.ai.model.dto.AiTaskCreated");
	}

}
