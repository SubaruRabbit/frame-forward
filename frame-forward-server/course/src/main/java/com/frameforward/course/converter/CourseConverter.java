package com.frameforward.course.converter;

import org.mapstruct.Mapper;

import com.frameforward.course.model.dto.ContentVersion;
import com.frameforward.course.model.entity.CourseContentVersionEntity;

@Mapper
public interface CourseConverter {

	ContentVersion toContentVersion(CourseContentVersionEntity source);

}
