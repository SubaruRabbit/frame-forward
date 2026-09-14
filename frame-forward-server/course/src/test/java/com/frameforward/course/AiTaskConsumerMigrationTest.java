package com.frameforward.course;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class AiTaskConsumerMigrationTest {

	@Test
	void servicesUseCanonicalRuntime() throws Exception {

		for (String name : new String[] {
				"CourseService"
		}) {
			Class<?> service = Class.forName("com.frameforward.course.service." + name);
			assertThat(Arrays.stream(service.getDeclaredFields()).map(field -> field.getType().getName()))
					.contains("com.frameforward.ai.service.AiTaskRuntime")
					.doesNotContain("com.frameforward.ai.AiTaskRuntime");
		}
	}

}
