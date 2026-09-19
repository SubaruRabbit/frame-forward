package com.frameforward.course.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.course.mapper.AssignmentFeedbackMapper;
import com.frameforward.course.mapper.CourseChapterMapper;
import com.frameforward.course.mapper.CourseContentVersionMapper;
import com.frameforward.course.mapper.CourseDefinitionMapper;
import com.frameforward.course.mapper.CourseLessonMapper;
import com.frameforward.course.mapper.LessonProgressMapper;
import com.frameforward.course.model.entity.AssignmentFeedbackEntity;
import com.frameforward.course.model.entity.CourseChapterEntity;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
import com.frameforward.course.model.entity.CourseDefinitionEntity;
import com.frameforward.course.model.entity.CourseLessonEntity;
import com.frameforward.course.model.entity.LessonProgressEntity;

@Repository
@lombok.RequiredArgsConstructor
public class CourseRepository {

	private final CourseContentVersionMapper versions;

	private final LessonProgressMapper progress;

	private final AssignmentFeedbackMapper feedback;

	private final CourseDefinitionMapper definitions;

	private final CourseChapterMapper chapters;

	private final CourseLessonMapper lessons;

	public List<CourseDefinitionEntity> listDefinitions() {
		return definitions
				.selectList(new LambdaQueryWrapper<CourseDefinitionEntity>().orderByAsc(CourseDefinitionEntity::getId));
	}

	public CourseDefinitionEntity findDefinition(String courseId) {
		return definitions.selectById(courseId);
	}

	public CourseContentVersionEntity findLatestVersion(String courseId) {
		return versions.selectOne(new LambdaQueryWrapper<CourseContentVersionEntity>()
				.eq(CourseContentVersionEntity::getCourseId, courseId)
				.inSql(CourseContentVersionEntity::getId, "SELECT content_version_id FROM course_chapters")
				.orderByDesc(CourseContentVersionEntity::getCreatedAt).last("LIMIT 1"));
	}

	public List<CourseChapterEntity> listChapters(String contentVersionId) {
		return chapters.selectList(new LambdaQueryWrapper<CourseChapterEntity>()
				.eq(CourseChapterEntity::getContentVersionId, contentVersionId)
				.orderByAsc(CourseChapterEntity::getSequenceNumber));
	}

	public List<CourseLessonEntity> listLessons(String chapterId) {
		return lessons.selectList(new LambdaQueryWrapper<CourseLessonEntity>()
				.eq(CourseLessonEntity::getChapterId, chapterId).orderByAsc(CourseLessonEntity::getSequenceNumber));
	}

	public long countLessons(String contentVersionId) {
		return listChapters(contentVersionId).stream().mapToLong(chapter -> listLessons(chapter.id).size()).sum();
	}

	public CourseContentVersionEntity findVersion(String courseId, String contentVersion) {
		return versions.selectOne(new LambdaQueryWrapper<CourseContentVersionEntity>()
				.eq(CourseContentVersionEntity::getCourseId, courseId)
				.eq(CourseContentVersionEntity::getContentVersion, contentVersion));
	}

	public void saveVersion(CourseContentVersionEntity entity) {
		versions.insert(entity);
	}

	public long countCompletedLessons(String accountId, String contentVersionId) {
		return progress.selectCount(
				new LambdaQueryWrapper<LessonProgressEntity>().eq(LessonProgressEntity::getAccountId, accountId)
						.eq(LessonProgressEntity::getContentVersionId, contentVersionId));
	}

	public long countLessonProgress(String accountId, String contentVersionId, String lessonId) {
		return progress.selectCount(
				new LambdaQueryWrapper<LessonProgressEntity>().eq(LessonProgressEntity::getAccountId, accountId)
						.eq(LessonProgressEntity::getContentVersionId, contentVersionId)
						.eq(LessonProgressEntity::getLessonId, lessonId));
	}

	public void saveProgress(LessonProgressEntity entity) {
		progress.insert(entity);
	}

	public void saveFeedback(AssignmentFeedbackEntity entity) {
		feedback.insert(entity);
	}

}
