package com.frameforward.course.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frameforward.course.model.entity.CourseLessonEntity;

@Mapper
public interface CourseLessonMapper extends BaseMapper<CourseLessonEntity> {
}
