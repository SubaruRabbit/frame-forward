package com.frameforward.bootstrap.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Component;

import com.frameforward.bootstrap.FrameForwardApplication;

class GeneratedMapperIsolationTest {

	@Test
	void mapstructConvertersAreSpringComponentsWhileMybatisScanningStaysAnnotationLimited() throws Exception {
		assertThat(
				Class.forName("com.frameforward.course.converter.CourseConverterImpl").getAnnotation(Component.class))
				.isNotNull();
		assertThat(FrameForwardApplication.class.getAnnotation(MapperScan.class).annotationClass())
				.isEqualTo(Mapper.class);
	}

}
