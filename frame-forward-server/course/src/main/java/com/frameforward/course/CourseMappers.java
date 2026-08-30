package com.frameforward.course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
@Mapper interface CourseContentVersionMapper extends BaseMapper<CourseContentVersionEntity> {}
@Mapper interface LessonProgressMapper extends BaseMapper<LessonProgressEntity> {}
@Mapper interface AssignmentFeedbackMapper extends BaseMapper<AssignmentFeedbackEntity> {}
