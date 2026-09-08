package com.frameforward.course.business;
import java.util.List;

import org.springframework.stereotype.Component;

import com.frameforward.course.component.CourseCatalog;
import com.frameforward.course.manager.CourseManager;
import com.frameforward.course.model.dto.Course;
import com.frameforward.course.model.dto.Lesson;
import com.frameforward.course.model.dto.LessonContext;
import com.frameforward.course.model.dto.ProgressSnapshot;
import com.frameforward.course.model.entity.CourseContentVersionEntity;

@Component
public class CourseBusiness {
    private final CourseManager manager;

    public CourseBusiness(CourseManager manager) {
        this.manager = manager;
    }

    public List<Course> catalog() {
        return CourseCatalog.p0();
    }

    public Course course(String courseId) {
        return catalog().stream().filter(course -> course.id().equals(courseId)).findFirst()
                .orElseThrow(CourseNotFound::new);
    }

    public ProgressSnapshot progress(String accountId, String courseId) {
        Course course = course(courseId);
        CourseContentVersionEntity version = manager.findOrCreateVersion(course);
        long completedLessons = manager.countCompletedLessons(accountId, version.id);
        return new ProgressSnapshot(courseId, version.contentVersion, Math.toIntExact(completedLessons),
                course.lessons().size());
    }

    public LessonContext lesson(String courseId, String lessonId) {
        Course course = course(courseId);
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
        Course course = course(courseId);
        if (nextContentVersion == null || nextContentVersion.isBlank()) {
            throw new IllegalArgumentException("content version is required");
        }
        return manager.findOrCreateVersion(course, nextContentVersion);
    }

}
