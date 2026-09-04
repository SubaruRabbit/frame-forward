package com.frameforward.course;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.media.MediaService;

@Service
public class CourseService {
    private final AuthService auth;
    private final MediaService media;
    private final AiTaskRuntime ai;
    private final CourseBusiness business;

    public CourseService(AuthService auth, MediaService media, AiTaskRuntime ai, CourseBusiness business) {
        this.auth = auth;
        this.media = media;
        this.ai = ai;
        this.business = business;
    }

    public List<CourseCatalog.Course> catalog(String token) {
        auth.requireAccountId(token);
        return business.catalog();
    }

    public CourseCatalog.Course course(String token, String courseId) {
        auth.requireAccountId(token);
        return business.course(courseId);
    }

    public Progress progress(String token, String courseId) {
        String account = auth.requireAccountId(token);
        return progressFor(account, courseId);
    }

    @Transactional
    public Feedback submit(String token, String courseId, String lessonId, String mediaId, String key) {
        String account = auth.requireAccountId(token);
        CourseBusiness.LessonContext lesson = business.lesson(courseId, lessonId);
        media.get(account, mediaId);
        AiTaskRuntime.CreateRequest request = new AiTaskRuntime.CreateRequest();
        request.operationType = "course-feedback";
        request.input = Map.of("lessonObjective", lesson.objective(), "mediaId", mediaId, "contentVersion",
                lesson.contentVersion());
        AiTaskRuntime.Created task = ai.create(token, key, request);
        business.recordSubmission(account, lesson, mediaId, task.taskId());
        return new Feedback(lesson.objective(), task.taskId(), progressFor(account, courseId));
    }

    /** 本地生成命令的服务入口；绝不更新既有内容版本。 */
    @Transactional
    public ContentVersion regenerate(String courseId, String nextContentVersion) {
        return contentVersion(business.regenerate(courseId, nextContentVersion));
    }

    private Progress progressFor(String account, String courseId) {
        CourseBusiness.ProgressSnapshot progress = business.progress(account, courseId);
        return new Progress(progress.courseId(), progress.contentVersion(), progress.completedLessons(),
                progress.totalLessons());
    }

    private ContentVersion contentVersion(CourseContentVersionEntity item) {
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
