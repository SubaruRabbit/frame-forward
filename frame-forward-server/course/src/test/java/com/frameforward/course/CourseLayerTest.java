package com.frameforward.course;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.frameforward.course.business.CourseBusiness;
import com.frameforward.course.business.CourseNotFound;
import com.frameforward.course.converter.CourseContentConverter;
import com.frameforward.course.manager.CourseManager;
import com.frameforward.course.mapper.AssignmentFeedbackMapper;
import com.frameforward.course.mapper.CourseChapterMapper;
import com.frameforward.course.mapper.CourseContentVersionMapper;
import com.frameforward.course.mapper.CourseDefinitionMapper;
import com.frameforward.course.mapper.CourseLessonMapper;
import com.frameforward.course.mapper.LessonProgressMapper;
import com.frameforward.course.model.dto.Chapter;
import com.frameforward.course.model.dto.CourseDetail;
import com.frameforward.course.model.dto.CourseSummary;
import com.frameforward.course.model.dto.Lesson;
import com.frameforward.course.model.entity.AssignmentFeedbackEntity;
import com.frameforward.course.model.entity.CourseChapterEntity;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
import com.frameforward.course.model.entity.CourseDefinitionEntity;
import com.frameforward.course.model.entity.CourseLessonEntity;
import com.frameforward.course.model.entity.LessonProgressEntity;
import com.frameforward.course.repository.CourseRepository;

class CourseLayerTest {

	@Test
	void managerCreatesVersionsRecordsProgressOnlyOnceAndSavesFeedback() {
		var versions = mock(CourseContentVersionMapper.class);
		var progress = mock(LessonProgressMapper.class);
		var feedback = mock(AssignmentFeedbackMapper.class);
		var manager = new CourseManager(
				new CourseRepository(versions, progress, feedback, mock(CourseDefinitionMapper.class),
						mock(CourseChapterMapper.class), mock(CourseLessonMapper.class)),
				mock(CourseContentConverter.class));
		var course = course();
		when(versions.selectOne(any())).thenReturn(null);

		var created = manager.findOrCreateVersion(course);

		assertEquals(course.id(), created.courseId);
		assertEquals(course.contentVersion(), created.contentVersion);
		verify(versions).insert(created);
		when(versions.selectOne(any())).thenReturn(created);
		assertSame(created, manager.findOrCreateVersion(course, "next"));
		when(progress.selectCount(any())).thenReturn(2L, 0L, 1L);
		assertEquals(2, manager.countCompletedLessons("account", created.id));
		manager.recordProgressIfAbsent("account", created.id, "lesson");
		manager.recordProgressIfAbsent("account", created.id, "lesson");
		var savedProgress = ArgumentCaptor.forClass(LessonProgressEntity.class);
		verify(progress).insert(savedProgress.capture());
		assertNotNull(savedProgress.getValue().id);
		manager.saveFeedback("account", created.id, "lesson", "media", "task", "objective");
		var saved = ArgumentCaptor.forClass(AssignmentFeedbackEntity.class);
		verify(feedback).insert(saved.capture());
		assertEquals("objective", saved.getValue().lessonObjective);
	}

	@Test
	void businessLooksUpCourseProgressLessonSubmissionAndRegeneration() {
		var manager = mock(CourseManager.class);
		var business = new CourseBusiness(manager);
		var course = course();
		var version = new CourseContentVersionEntity();
		version.id = "version";
		version.contentVersion = course.contentVersion();
		when(manager.course(course.id())).thenReturn(course);
		when(manager.catalog()).thenReturn(
				List.of(new CourseSummary(course.id(), course.title(), course.category(), course.contentVersion(), 1)));
		when(manager.findOrCreateVersion(any(CourseDetail.class))).thenReturn(version);
		when(manager.countCompletedLessons("account", "version")).thenReturn(1L);

		assertEquals(1, business.catalog().size());
		assertEquals(course.id(), business.course(course.id()).id());
		assertEquals(1, business.progress("account", course.id()).completedLessons());
		var lesson = business.lesson(course.id(), course.lessons().getFirst().id());
		business.recordSubmission("account", lesson, "media", "task");
		verify(manager).recordProgressIfAbsent("account", "version", lesson.lessonId());
		verify(manager).saveFeedback("account", "version", lesson.lessonId(), "media", "task", lesson.objective());
		when(manager.findOrCreateVersion(any(CourseDetail.class), eq("v2"))).thenReturn(version);
		assertSame(version, business.regenerate(course.id(), "v2"));
		assertThrows(IllegalArgumentException.class, () -> business.regenerate(course.id(), " "));
		assertThrows(CourseNotFound.class, () -> business.course("missing"));
		assertThrows(CourseNotFound.class, () -> business.lesson(course.id(), "missing"));
	}

	@Test
	void managerLoadsTheStructuredCatalogAndCourseDetailFromPersistence() {
		var versions = mock(CourseContentVersionMapper.class);
		var progress = mock(LessonProgressMapper.class);
		var feedback = mock(AssignmentFeedbackMapper.class);
		var definitions = mock(CourseDefinitionMapper.class);
		var chapters = mock(CourseChapterMapper.class);
		var lessons = mock(CourseLessonMapper.class);
		var converter = mock(CourseContentConverter.class);
		var manager = new CourseManager(
				new CourseRepository(versions, progress, feedback, definitions, chapters, lessons), converter);
		var definition = new CourseDefinitionEntity();
		definition.id = "course";
		definition.title = "课程";
		definition.category = "BASICS";
		var version = new CourseContentVersionEntity();
		version.id = "version";
		version.contentVersion = "v1";
		var chapter = new CourseChapterEntity();
		chapter.id = "chapter";
		chapter.title = "章节";
		chapter.sequenceNumber = 1;
		var lesson = new CourseLessonEntity();
		lesson.id = "lesson";
		when(definitions.selectList(any())).thenReturn(List.of(definition));
		when(definitions.selectById("course")).thenReturn(definition);
		when(versions.selectOne(any())).thenReturn(version);
		when(chapters.selectList(any())).thenReturn(List.of(chapter));
		when(lessons.selectList(any())).thenReturn(List.of(lesson));
		when(converter.toLesson(lesson)).thenReturn(new Lesson("lesson", "课时", "目标"));

		assertEquals("v1", manager.catalog().getFirst().contentVersion());
		assertEquals(1, manager.catalog().getFirst().lessonCount());
		assertEquals("课时", manager.course("course").chapters().getFirst().lessons().getFirst().title());
	}

	private static CourseDetail course() {
		return new CourseDetail("course", "课程", "BASICS", "v1",
				List.of(new Chapter("chapter", "章节", 1, List.of(new Lesson("lesson", "课时", "objective")))));
	}

}
