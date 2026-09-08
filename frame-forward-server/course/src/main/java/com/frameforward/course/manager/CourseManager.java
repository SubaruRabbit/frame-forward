package com.frameforward.course.manager;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.frameforward.course.model.dto.Course;
import com.frameforward.course.model.entity.AssignmentFeedbackEntity;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
import com.frameforward.course.model.entity.LessonProgressEntity;
import com.frameforward.course.repository.CourseRepository;

@Component
public class CourseManager {
    private static final String MODEL_ID = "qwen3.7-plus";
    private static final String PROMPT_VERSION = "p0-v1";
    private static final String SOURCE_MATERIAL_VERSION = "p0-2026-01";

    private final CourseRepository repository;

    public CourseManager(CourseRepository repository) {
        this.repository = repository;
    }

    public CourseContentVersionEntity findOrCreateVersion(Course course) {
        return findOrCreateVersion(course, course.contentVersion());
    }

    public CourseContentVersionEntity findOrCreateVersion(Course course, String contentVersion) {
        CourseContentVersionEntity existing = repository.findVersion(course.id(), contentVersion);
        if (existing != null) {
            return existing;
        }
        CourseContentVersionEntity created = new CourseContentVersionEntity();
        created.id = UUID.randomUUID().toString();
        created.courseId = course.id();
        created.contentVersion = contentVersion;
        created.modelId = MODEL_ID;
        created.promptVersion = PROMPT_VERSION;
        created.sourceMaterialVersion = SOURCE_MATERIAL_VERSION;
        created.createdAt = Instant.now();
        repository.saveVersion(created);
        return created;
    }

    public long countCompletedLessons(String accountId, String contentVersionId) {
        return repository.countCompletedLessons(accountId, contentVersionId);
    }

    public void recordProgressIfAbsent(String accountId, String contentVersionId, String lessonId) {
        long existing = repository.countLessonProgress(accountId, contentVersionId, lessonId);
        if (existing == 0) {
            LessonProgressEntity item = new LessonProgressEntity();
            item.accountId = accountId;
            item.contentVersionId = contentVersionId;
            item.lessonId = lessonId;
            item.completedAt = Instant.now();
            repository.saveProgress(item);
        }
    }

    public void saveFeedback(String accountId, String contentVersionId, String lessonId, String mediaId,
            String feedbackTaskId, String lessonObjective) {
        AssignmentFeedbackEntity entry = new AssignmentFeedbackEntity();
        entry.id = UUID.randomUUID().toString();
        entry.accountId = accountId;
        entry.contentVersionId = contentVersionId;
        entry.lessonId = lessonId;
        entry.mediaId = mediaId;
        entry.feedbackTaskId = feedbackTaskId;
        entry.lessonObjective = lessonObjective;
        entry.createdAt = Instant.now();
        repository.saveFeedback(entry);
    }
}
