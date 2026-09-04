package com.frameforward.course;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
class CourseBusiness {
    private final CourseManager manager;

    CourseBusiness(CourseManager manager) {
        this.manager = manager;
    }

    List<CourseCatalog.Course> catalog() {
        return CourseCatalog.p0();
    }

    CourseCatalog.Course course(String courseId) {
        return catalog().stream().filter(course -> course.id().equals(courseId)).findFirst()
                .orElseThrow(CourseService.NotFound::new);
    }

    ProgressSnapshot progress(String accountId, String courseId) {
        CourseCatalog.Course course = course(courseId);
        CourseContentVersionEntity version = manager.findOrCreateVersion(course);
        long completedLessons = manager.countCompletedLessons(accountId, version.id);
        return new ProgressSnapshot(courseId, version.contentVersion, Math.toIntExact(completedLessons),
                course.lessons().size());
    }

    LessonContext lesson(String courseId, String lessonId) {
        CourseCatalog.Course course = course(courseId);
        CourseCatalog.Lesson lesson = course.lessons().stream().filter(item -> item.id().equals(lessonId)).findFirst()
                .orElseThrow(CourseService.NotFound::new);
        CourseContentVersionEntity version = manager.findOrCreateVersion(course);
        return new LessonContext(version, lesson.id(), lesson.objective());
    }

    void recordSubmission(String accountId, LessonContext lesson, String mediaId, String feedbackTaskId) {
        manager.recordProgressIfAbsent(accountId, lesson.version().id, lesson.lessonId());
        manager.saveFeedback(accountId, lesson.version().id, lesson.lessonId(), mediaId, feedbackTaskId,
                lesson.objective());
    }

    CourseContentVersionEntity regenerate(String courseId, String nextContentVersion) {
        CourseCatalog.Course course = course(courseId);
        if (nextContentVersion == null || nextContentVersion.isBlank()) {
            throw new IllegalArgumentException("content version is required");
        }
        return manager.findOrCreateVersion(course, nextContentVersion);
    }

    record ProgressSnapshot(String courseId, String contentVersion, int completedLessons, int totalLessons) {
    }

    record LessonContext(CourseContentVersionEntity version, String lessonId, String objective) {
        String contentVersion() {
            return version.contentVersion;
        }
    }
}
