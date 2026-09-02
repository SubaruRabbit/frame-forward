package com.frameforward.course;

import java.time.Instant;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.media.MediaService;

@Service
public class CourseService {
    private final AuthService auth;
    private final MediaService media;
    private final AiTaskRuntime ai;
    private final CourseContentVersionMapper versions;
    private final LessonProgressMapper progress;
    private final AssignmentFeedbackMapper feedback;
    public CourseService(AuthService auth, MediaService media, AiTaskRuntime ai, CourseContentVersionMapper versions,
            LessonProgressMapper progress, AssignmentFeedbackMapper feedback) {
        this.auth = auth;
        this.media = media;
        this.ai = ai;
        this.versions = versions;
        this.progress = progress;
        this.feedback = feedback;
    }
    public List<CourseCatalog.Course> catalog(String token) {
        auth.requireAccountId(token);
        return CourseCatalog.p0();
    }
    public CourseCatalog.Course course(String token, String courseId) {
        auth.requireAccountId(token);
        return CourseCatalog.p0().stream().filter(c -> c.id().equals(courseId)).findFirst().orElseThrow(NotFound::new);
    }
    public Progress progress(String token, String courseId) {
        var account = auth.requireAccountId(token);
        var course = course(token, courseId);
        var version = versionFor(course);
        var done = progress.selectCount(
                new LambdaQueryWrapper<LessonProgressEntity>().eq(LessonProgressEntity::getAccountId, account)
                        .eq(LessonProgressEntity::getContentVersionId, version.id));
        return new Progress(courseId, version.contentVersion, done.intValue(), course.lessons().size());
    }
    @Transactional
    public Feedback submit(String token, String courseId, String lessonId, String mediaId, String key) {
        var account = auth.requireAccountId(token);
        var current = course(token, courseId);
        var lesson = current.lessons().stream().filter(l -> l.id().equals(lessonId)).findFirst()
                .orElseThrow(NotFound::new);
        media.get(account, mediaId);
        var version = versionFor(current);
        var request = new AiTaskRuntime.CreateRequest();
        request.operationType = "course-feedback";
        request.input = Map.of("lessonObjective", lesson.objective(), "mediaId", mediaId, "contentVersion",
                version.contentVersion);
        var task = ai.create(token, key, request);
        if (progress.selectCount(
                new LambdaQueryWrapper<LessonProgressEntity>().eq(LessonProgressEntity::getAccountId, account)
                        .eq(LessonProgressEntity::getContentVersionId, version.id)
                        .eq(LessonProgressEntity::getLessonId, lessonId)) == 0) {
            var item = new LessonProgressEntity();
            item.accountId = account;
            item.contentVersionId = version.id;
            item.lessonId = lessonId;
            item.completedAt = Instant.now();
            progress.insert(item);
        }
        var entry = new AssignmentFeedbackEntity();
        entry.id = UUID.randomUUID().toString();
        entry.accountId = account;
        entry.contentVersionId = version.id;
        entry.lessonId = lessonId;
        entry.mediaId = mediaId;
        entry.feedbackTaskId = task.taskId();
        entry.lessonObjective = lesson.objective();
        entry.createdAt = Instant.now();
        feedback.insert(entry);
        return new Feedback(lesson.objective(), task.taskId(), progress(token, courseId));
    }
    private CourseContentVersionEntity versionFor(CourseCatalog.Course course) {
        var found = versions.selectOne(new LambdaQueryWrapper<CourseContentVersionEntity>()
                .eq(CourseContentVersionEntity::getCourseId, course.id())
                .eq(CourseContentVersionEntity::getContentVersion, course.contentVersion()));
        if (found != null)
            return found;
        var created = new CourseContentVersionEntity();
        created.id = UUID.randomUUID().toString();
        created.courseId = course.id();
        created.contentVersion = course.contentVersion();
        created.modelId = "qwen3.7-plus";
        created.promptVersion = "p0-v1";
        created.sourceMaterialVersion = "p0-2026-01";
        created.createdAt = Instant.now();
        versions.insert(created);
        return created;
    }
    /** 本地生成命令的服务入口；绝不更新既有内容版本。 */
    @Transactional
    public ContentVersion regenerate(String courseId, String nextContentVersion) {
        var course = CourseCatalog.p0().stream().filter(item -> item.id().equals(courseId)).findFirst()
                .orElseThrow(NotFound::new);
        if (nextContentVersion == null || nextContentVersion.isBlank())
            throw new IllegalArgumentException("content version is required");
        var existing = versions.selectOne(new LambdaQueryWrapper<CourseContentVersionEntity>()
                .eq(CourseContentVersionEntity::getCourseId, courseId)
                .eq(CourseContentVersionEntity::getContentVersion, nextContentVersion));
        if (existing != null)
            return info(existing);
        var created = new CourseContentVersionEntity();
        created.id = UUID.randomUUID().toString();
        created.courseId = course.id();
        created.contentVersion = nextContentVersion;
        created.modelId = "qwen3.7-plus";
        created.promptVersion = "p0-v1";
        created.sourceMaterialVersion = "p0-2026-01";
        created.createdAt = Instant.now();
        versions.insert(created);
        return info(created);
    }
    private ContentVersion info(CourseContentVersionEntity item) {
        return new ContentVersion(item.id, item.courseId, item.contentVersion, item.modelId, item.promptVersion,
                item.sourceMaterialVersion);
    }
    public record Progress(String courseId, String contentVersion, int completedLessons, int totalLessons) {
    }
    public record Feedback(String lessonObjective, String feedbackTaskId, Progress progress) {
    }
    public record ContentVersion(String id, String courseId, String contentVersion, String modelId,
            String promptVersion, String sourceMaterialVersion) {
    }
    public static class NotFound extends RuntimeException {
    }
}
