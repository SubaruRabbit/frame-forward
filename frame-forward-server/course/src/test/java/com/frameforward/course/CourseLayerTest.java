package com.frameforward.course;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.frameforward.course.business.CourseBusiness;
import com.frameforward.course.business.CourseNotFound;
import com.frameforward.course.component.CourseCatalog;
import com.frameforward.course.manager.CourseManager;
import com.frameforward.course.mapper.AssignmentFeedbackMapper;
import com.frameforward.course.mapper.CourseContentVersionMapper;
import com.frameforward.course.mapper.LessonProgressMapper;
import com.frameforward.course.model.dto.Course;
import com.frameforward.course.model.entity.AssignmentFeedbackEntity;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
import com.frameforward.course.model.entity.LessonProgressEntity;

class CourseLayerTest {
    @Test
    void managerCreatesVersionsRecordsProgressOnlyOnceAndSavesFeedback() {
        var versions = mock(CourseContentVersionMapper.class);
        var progress = mock(LessonProgressMapper.class);
        var feedback = mock(AssignmentFeedbackMapper.class);
        var manager = new CourseManager(
                new com.frameforward.course.repository.CourseRepository(versions, progress, feedback));
        var course = CourseCatalog.p0().getFirst();
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
        verify(progress).insert(any(LessonProgressEntity.class));
        manager.saveFeedback("account", created.id, "lesson", "media", "task", "objective");
        var saved = ArgumentCaptor.forClass(AssignmentFeedbackEntity.class);
        verify(feedback).insert(saved.capture());
        assertEquals("objective", saved.getValue().lessonObjective);
    }

    @Test
    void businessLooksUpCourseProgressLessonSubmissionAndRegeneration() {
        var manager = mock(CourseManager.class);
        var business = new CourseBusiness(manager);
        var course = business.catalog().getFirst();
        var version = new CourseContentVersionEntity();
        version.id = "version";
        version.contentVersion = course.contentVersion();
        when(manager.findOrCreateVersion(any(Course.class))).thenReturn(version);
        when(manager.countCompletedLessons("account", "version")).thenReturn(1L);

        assertEquals(course.id(), business.course(course.id()).id());
        assertEquals(1, business.progress("account", course.id()).completedLessons());
        var lesson = business.lesson(course.id(), course.lessons().getFirst().id());
        business.recordSubmission("account", lesson, "media", "task");
        verify(manager).recordProgressIfAbsent("account", "version", lesson.lessonId());
        verify(manager).saveFeedback("account", "version", lesson.lessonId(), "media", "task", lesson.objective());
        when(manager.findOrCreateVersion(any(Course.class), eq("v2"))).thenReturn(version);
        assertSame(version, business.regenerate(course.id(), "v2"));
        assertThrows(IllegalArgumentException.class, () -> business.regenerate(course.id(), " "));
        assertThrows(CourseNotFound.class, () -> business.course("missing"));
        assertThrows(CourseNotFound.class, () -> business.lesson(course.id(), "missing"));
    }
}
