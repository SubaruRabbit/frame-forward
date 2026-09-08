package com.frameforward.course.service;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.service.AiTaskRuntime;
import com.frameforward.auth.service.AuthService;
import com.frameforward.course.business.CourseBusiness;
import com.frameforward.course.model.dto.ContentVersion;
import com.frameforward.course.model.dto.Course;
import com.frameforward.course.model.dto.Feedback;
import com.frameforward.course.model.dto.LessonContext;
import com.frameforward.course.model.dto.Progress;
import com.frameforward.course.model.dto.ProgressSnapshot;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
import com.frameforward.media.service.MediaService;

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

    public List<Course> catalog(String token) {
        auth.requireAccountId(token);
        return business.catalog();
    }

    public Course course(String token, String courseId) {
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
        LessonContext lesson = business.lesson(courseId, lessonId);
        media.get(account, mediaId);
        AiTaskCreateRequest request = new AiTaskCreateRequest();
        request.operationType = "course-feedback";
        request.input = Map.of("lessonObjective", lesson.objective(), "mediaId", mediaId, "contentVersion",
                lesson.contentVersion());
        AiTaskCreated task = ai.create(token, key, request);
        business.recordSubmission(account, lesson, mediaId, task.taskId());
        return new Feedback(lesson.objective(), task.taskId(), progressFor(account, courseId));
    }

    /** 本地生成命令的服务入口；绝不更新既有内容版本。 */
    @Transactional
    public ContentVersion regenerate(String courseId, String nextContentVersion) {
        return contentVersion(business.regenerate(courseId, nextContentVersion));
    }

    private Progress progressFor(String account, String courseId) {
        ProgressSnapshot progress = business.progress(account, courseId);
        return new Progress(progress.courseId(), progress.contentVersion(), progress.completedLessons(),
                progress.totalLessons());
    }

    private ContentVersion contentVersion(CourseContentVersionEntity item) {
        return new ContentVersion(item.id, item.courseId, item.contentVersion, item.modelId, item.promptVersion,
                item.sourceMaterialVersion);
    }

}
