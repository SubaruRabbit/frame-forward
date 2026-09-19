package com.frameforward.course.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frameforward.course.model.entity.CourseChapterEntity;

@Mapper
public interface CourseChapterMapper extends BaseMapper<CourseChapterEntity> {
}
