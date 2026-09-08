package com.frameforward.course.repository;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.course.mapper.AssignmentFeedbackMapper;
import com.frameforward.course.mapper.CourseContentVersionMapper;
import com.frameforward.course.mapper.LessonProgressMapper;
import com.frameforward.course.model.entity.AssignmentFeedbackEntity;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
import com.frameforward.course.model.entity.LessonProgressEntity;

@Repository
public class CourseRepository {
    private final CourseContentVersionMapper versions;
    private final LessonProgressMapper progress;
    private final AssignmentFeedbackMapper feedback;
    public CourseRepository(CourseContentVersionMapper versions, LessonProgressMapper progress,
            AssignmentFeedbackMapper feedback) {
        this.versions = versions;
        this.progress = progress;
        this.feedback = feedback;
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
