package com.frameforward.course;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Component
class CourseManager {
    private static final String MODEL_ID = "qwen3.7-plus";
    private static final String PROMPT_VERSION = "p0-v1";
    private static final String SOURCE_MATERIAL_VERSION = "p0-2026-01";

    private final CourseContentVersionMapper versions;
    private final LessonProgressMapper progress;
    private final AssignmentFeedbackMapper feedback;

    CourseManager(CourseContentVersionMapper versions, LessonProgressMapper progress,
            AssignmentFeedbackMapper feedback) {
        this.versions = versions;
        this.progress = progress;
        this.feedback = feedback;
    }

    CourseContentVersionEntity findOrCreateVersion(CourseCatalog.Course course) {
        return findOrCreateVersion(course, course.contentVersion());
    }

    CourseContentVersionEntity findOrCreateVersion(CourseCatalog.Course course, String contentVersion) {
        CourseContentVersionEntity existing = versions.selectOne(new LambdaQueryWrapper<CourseContentVersionEntity>()
                .eq(CourseContentVersionEntity::getCourseId, course.id())
                .eq(CourseContentVersionEntity::getContentVersion, contentVersion));
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
        versions.insert(created);
        return created;
    }

    long countCompletedLessons(String accountId, String contentVersionId) {
        return progress.selectCount(
                new LambdaQueryWrapper<LessonProgressEntity>().eq(LessonProgressEntity::getAccountId, accountId)
                        .eq(LessonProgressEntity::getContentVersionId, contentVersionId));
    }

    void recordProgressIfAbsent(String accountId, String contentVersionId, String lessonId) {
        long existing = progress.selectCount(
                new LambdaQueryWrapper<LessonProgressEntity>().eq(LessonProgressEntity::getAccountId, accountId)
                        .eq(LessonProgressEntity::getContentVersionId, contentVersionId)
                        .eq(LessonProgressEntity::getLessonId, lessonId));
        if (existing == 0) {
            LessonProgressEntity item = new LessonProgressEntity();
            item.accountId = accountId;
            item.contentVersionId = contentVersionId;
            item.lessonId = lessonId;
            item.completedAt = Instant.now();
            progress.insert(item);
        }
    }

    void saveFeedback(String accountId, String contentVersionId, String lessonId, String mediaId, String feedbackTaskId,
            String lessonObjective) {
        AssignmentFeedbackEntity entry = new AssignmentFeedbackEntity();
        entry.id = UUID.randomUUID().toString();
        entry.accountId = accountId;
        entry.contentVersionId = contentVersionId;
        entry.lessonId = lessonId;
        entry.mediaId = mediaId;
        entry.feedbackTaskId = feedbackTaskId;
        entry.lessonObjective = lessonObjective;
        entry.createdAt = Instant.now();
        feedback.insert(entry);
    }
}
