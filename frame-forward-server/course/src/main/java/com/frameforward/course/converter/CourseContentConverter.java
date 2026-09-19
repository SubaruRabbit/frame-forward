package com.frameforward.course.converter;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.course.model.dto.JudgmentExercise;
import com.frameforward.course.model.dto.Lesson;
import com.frameforward.course.model.entity.CourseLessonEntity;

@Component
@lombok.RequiredArgsConstructor
public class CourseContentConverter {

	private final ObjectMapper objectMapper;

	public Lesson toLesson(CourseLessonEntity lesson) {

		try {
			return new Lesson(lesson.id, lesson.title, lesson.objective, lesson.content, lesson.correctExample,
					lesson.incorrectExample, objectMapper.readValue(lesson.exerciseJson, JudgmentExercise.class),
					lesson.assignmentText);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("课程练习内容格式无效：" + lesson.id, exception);
		}
	}

}
