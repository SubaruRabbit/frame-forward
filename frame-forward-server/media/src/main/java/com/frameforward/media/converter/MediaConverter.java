package com.frameforward.media.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.frameforward.media.model.dto.MediaResponse;
import com.frameforward.media.model.entity.MediaEntity;

@Mapper
public interface MediaConverter {

	@Mapping(target = "status", constant = "COMPLETED")
	@Mapping(target = "exif", source = "exifJson")
	MediaResponse toResponse(MediaEntity source);

}
