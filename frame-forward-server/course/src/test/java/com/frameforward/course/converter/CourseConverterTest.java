package com.frameforward.course.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.frameforward.course.model.entity.AssignmentFeedbackEntity;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
import com.frameforward.course.model.entity.LessonProgressEntity;

class CourseConverterTest {

	private final CourseConverter converter = Mappers.getMapper(CourseConverter.class);

	@Test
	void convertsContentVersionFieldByField() {
		var entity = CourseContentVersionEntity.builder().id("version-id").courseId("course").contentVersion("v2")
				.modelId("model").promptVersion("prompt").sourceMaterialVersion("source").build();

		var response = converter.toContentVersion(entity);

		assertThat(response.id()).isEqualTo("version-id");
		assertThat(response.courseId()).isEqualTo("course");
		assertThat(response.contentVersion()).isEqualTo("v2");
		assertThat(response.modelId()).isEqualTo("model");
		assertThat(response.promptVersion()).isEqualTo("prompt");
		assertThat(response.sourceMaterialVersion()).isEqualTo("source");
	}

	@Test
	void buildsEveryCourseEntityAndKeepsMybatisAccessors() {
		var progress = LessonProgressEntity.builder().accountId("account").contentVersionId("version")
				.lessonId("lesson").build();
		var feedback = AssignmentFeedbackEntity.builder().id("feedback").accountId("account")
				.contentVersionId("version").lessonId("lesson").mediaId("media").build();

		assertThat(progress.getAccountId()).isEqualTo("account");
		assertThat(progress.getContentVersionId()).isEqualTo("version");
		assertThat(progress.getLessonId()).isEqualTo("lesson");
		assertThat(feedback.getId()).isEqualTo("feedback");
	}

}
