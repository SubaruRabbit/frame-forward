package com.frameforward.media.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.media.model.entity.MediaEntity;

class MediaConverterTest {

	private final MediaConverter converter = Mappers.getMapper(MediaConverter.class);

	@Test
	void convertsEntityAndKeepsThePublicResponseContract() throws Exception {
		var entity = MediaEntity.builder().id("media").ownerId("owner").contentHash("hash").width(1920).height(1080)
				.originalPath("original").aiCopyPath("ai").exifJson("{\"iso\":\"100\"}").build();

		var response = converter.toResponse(entity);

		assertThat(response.id()).isEqualTo("media");
		assertThat(response.width()).isEqualTo(1920);
		assertThat(response.height()).isEqualTo(1080);
		assertThat(response.contentHash()).isEqualTo("hash");
		assertThat(response.status()).isEqualTo("COMPLETED");
		assertThat(response.exif()).isEqualTo("{\"iso\":\"100\"}");
		assertThat(new ObjectMapper().writeValueAsString(response)).isEqualTo(
				"{\"id\":\"media\",\"width\":1920,\"height\":1080,\"contentHash\":\"hash\",\"status\":\"COMPLETED\",\"exif\":\"{\\\"iso\\\":\\\"100\\\"}\"}");
	}

}
