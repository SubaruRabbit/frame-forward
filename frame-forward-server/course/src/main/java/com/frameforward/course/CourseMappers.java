package com.frameforward.course;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
@Mapper
interface CourseContentVersionMapper extends BaseMapper<CourseContentVersionEntity> {
}
@Mapper
interface LessonProgressMapper extends BaseMapper<LessonProgressEntity> {
}
@Mapper
interface AssignmentFeedbackMapper extends BaseMapper<AssignmentFeedbackEntity> {
}
