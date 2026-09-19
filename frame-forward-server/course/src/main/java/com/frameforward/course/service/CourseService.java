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
import com.frameforward.course.converter.CourseConverter;
import com.frameforward.course.model.dto.ContentVersion;
import com.frameforward.course.model.dto.CourseDetail;
import com.frameforward.course.model.dto.CourseSummary;
import com.frameforward.course.model.dto.Feedback;
import com.frameforward.course.model.dto.LessonContext;
import com.frameforward.course.model.dto.Progress;
import com.frameforward.course.model.dto.ProgressSnapshot;
import com.frameforward.media.service.MediaService;

@Service
@lombok.RequiredArgsConstructor
public class CourseService {

	private final AuthService auth;

	private final MediaService media;

	private final AiTaskRuntime ai;

	private final CourseBusiness business;

	private final CourseConverter converter;

	public List<CourseSummary> catalog(String token) {
		auth.requireAccountId(token);
		return business.catalog();
	}

	public CourseDetail course(String token, String courseId) {
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
		AiTaskCreateRequest request = AiTaskCreateRequest.builder().operationType("course-feedback")
				.input(Map.of("lessonObjective", lesson.objective(), "mediaId", mediaId, "contentVersion",
						lesson.contentVersion()))
				.build();
		AiTaskCreated task = ai.create(token, key, request);
		business.recordSubmission(account, lesson, mediaId, task.taskId());
		return new Feedback(lesson.objective(), task.taskId(), progressFor(account, courseId));
	}

	/** 本地生成命令的服务入口；绝不更新既有内容版本。 */
	@Transactional
	public ContentVersion regenerate(String courseId, String nextContentVersion) {
		return converter.toContentVersion(business.regenerate(courseId, nextContentVersion));
	}

	private Progress progressFor(String account, String courseId) {
		ProgressSnapshot progress = business.progress(account, courseId);
		return new Progress(progress.courseId(), progress.contentVersion(), progress.completedLessons(),
				progress.totalLessons());
	}

}
