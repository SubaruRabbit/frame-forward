package com.frameforward.course.business;

import java.util.List;

import org.springframework.stereotype.Component;

import com.frameforward.course.manager.CourseManager;
import com.frameforward.course.model.dto.CourseDetail;
import com.frameforward.course.model.dto.CourseSummary;
import com.frameforward.course.model.dto.Lesson;
import com.frameforward.course.model.dto.LessonContext;
import com.frameforward.course.model.dto.ProgressSnapshot;
import com.frameforward.course.model.entity.CourseContentVersionEntity;

@Component
@lombok.RequiredArgsConstructor
public class CourseBusiness {

	private final CourseManager manager;

	public List<CourseSummary> catalog() {
		return manager.catalog();
	}

	public CourseDetail course(String courseId) {
		CourseDetail course = manager.course(courseId);

		if (course == null) {
			throw new CourseNotFound();
		}
		return course;
	}

	public ProgressSnapshot progress(String accountId, String courseId) {
		CourseDetail course = course(courseId);
		CourseContentVersionEntity version = manager.findOrCreateVersion(course);
		long completedLessons = manager.countCompletedLessons(accountId, version.id);
		return new ProgressSnapshot(courseId, version.contentVersion, Math.toIntExact(completedLessons),
				course.lessons().size());
	}

	public LessonContext lesson(String courseId, String lessonId) {
		CourseDetail course = course(courseId);
		Lesson lesson = course.lessons().stream().filter(item -> item.id().equals(lessonId)).findFirst()
				.orElseThrow(CourseNotFound::new);
		CourseContentVersionEntity version = manager.findOrCreateVersion(course);
		return new LessonContext(version, lesson.id(), lesson.objective());
	}

	public void recordSubmission(String accountId, LessonContext lesson, String mediaId, String feedbackTaskId) {
		manager.recordProgressIfAbsent(accountId, lesson.version().id, lesson.lessonId());
		manager.saveFeedback(accountId, lesson.version().id, lesson.lessonId(), mediaId, feedbackTaskId,
				lesson.objective());
	}

	public CourseContentVersionEntity regenerate(String courseId, String nextContentVersion) {
		CourseDetail course = course(courseId);

		if (nextContentVersion == null || nextContentVersion.isBlank()) {
			throw new IllegalArgumentException("content version is required");
		}
		return manager.findOrCreateVersion(course, nextContentVersion);
	}

}
