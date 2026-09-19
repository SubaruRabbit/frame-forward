package com.frameforward.course.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.course.model.entity.AssignmentFeedbackEntity;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
import com.frameforward.course.model.entity.CourseLessonEntity;
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
		var progress = LessonProgressEntity.builder().id("progress").accountId("account").contentVersionId("version")
				.lessonId("lesson").build();
		var feedback = AssignmentFeedbackEntity.builder().id("feedback").accountId("account")
				.contentVersionId("version").lessonId("lesson").mediaId("media").build();

		assertThat(progress.getId()).isEqualTo("progress");
		assertThat(progress.getAccountId()).isEqualTo("account");
		assertThat(progress.getContentVersionId()).isEqualTo("version");
		assertThat(progress.getLessonId()).isEqualTo("lesson");
		assertThat(feedback.getId()).isEqualTo("feedback");
	}

	@Test
	void convertsStructuredLessonContentAndRejectsInvalidExerciseJson() {
		var source = new CourseLessonEntity();
		source.id = "lesson";
		source.title = "课时";
		source.objective = "目标";
		source.content = "正文";
		source.correctExample = "正确";
		source.incorrectExample = "错误";
		source.exerciseJson = "{\"question\":\"判断题\",\"answer\":true,\"explanation\":\"说明\"}";
		source.assignmentText = "作业";
		var contentConverter = new CourseContentConverter(new ObjectMapper());

		var lesson = contentConverter.toLesson(source);

		assertThat(lesson.exercise().answer()).isTrue();
		assertThat(lesson.assignment()).isEqualTo("作业");
		source.exerciseJson = "invalid";
		assertThatThrownBy(() -> contentConverter.toLesson(source)).isInstanceOf(IllegalStateException.class);
	}

}
