package com.frameforward.course.manager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.frameforward.course.business.CourseNotFound;
import com.frameforward.course.converter.CourseContentConverter;
import com.frameforward.course.model.dto.Chapter;
import com.frameforward.course.model.dto.CourseDetail;
import com.frameforward.course.model.dto.CourseSummary;
import com.frameforward.course.model.entity.AssignmentFeedbackEntity;
import com.frameforward.course.model.entity.CourseChapterEntity;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
import com.frameforward.course.model.entity.CourseDefinitionEntity;
import com.frameforward.course.model.entity.LessonProgressEntity;
import com.frameforward.course.repository.CourseRepository;

@Component
@lombok.RequiredArgsConstructor
public class CourseManager {

	private static final String MODEL_ID = "qwen3.7-plus";

	private static final String PROMPT_VERSION = "p0-v1";

	private static final String SOURCE_MATERIAL_VERSION = "p0-2026-01";

	private final CourseRepository repository;

	private final CourseContentConverter converter;

	public List<CourseSummary> catalog() {
		return repository.listDefinitions().stream().map(this::toSummary).toList();
	}

	public CourseDetail course(String courseId) {
		CourseDefinitionEntity definition = repository.findDefinition(courseId);
		CourseContentVersionEntity version = repository.findLatestVersion(courseId);

		if (definition == null || version == null) {
			throw new CourseNotFound();
		}

		return new CourseDetail(definition.id, definition.title, definition.category, version.contentVersion,
				repository.listChapters(version.id).stream().map(this::toChapter).toList());
	}

	public CourseContentVersionEntity findOrCreateVersion(CourseDetail course) {
		return findOrCreateVersion(course, course.contentVersion());
	}

	public CourseContentVersionEntity findOrCreateVersion(CourseDetail course, String contentVersion) {
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

	private CourseSummary toSummary(CourseDefinitionEntity definition) {
		CourseContentVersionEntity version = repository.findLatestVersion(definition.id);

		if (version == null) {
			throw new IllegalStateException("课程缺少内容版本：" + definition.id);
		}

		return new CourseSummary(definition.id, definition.title, definition.category, version.contentVersion,
				Math.toIntExact(repository.countLessons(version.id)));
	}

	private Chapter toChapter(CourseChapterEntity chapter) {
		return new Chapter(chapter.id, chapter.title, chapter.sequenceNumber,
				repository.listLessons(chapter.id).stream().map(converter::toLesson).toList());
	}

	public long countCompletedLessons(String accountId, String contentVersionId) {
		return repository.countCompletedLessons(accountId, contentVersionId);
	}

	public void recordProgressIfAbsent(String accountId, String contentVersionId, String lessonId) {
		long existing = repository.countLessonProgress(accountId, contentVersionId, lessonId);

		if (existing == 0) {
			LessonProgressEntity item = new LessonProgressEntity();
			item.id = UUID.randomUUID().toString();
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
