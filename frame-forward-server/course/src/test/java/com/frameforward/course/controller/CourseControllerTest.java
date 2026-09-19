package com.frameforward.course.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.frameforward.course.business.CourseNotFound;
import com.frameforward.course.model.dto.Assignment;
import com.frameforward.course.model.dto.CourseDetail;
import com.frameforward.course.model.dto.CourseSummary;
import com.frameforward.course.model.dto.Feedback;
import com.frameforward.course.model.dto.Progress;
import com.frameforward.course.service.CourseService;

class CourseControllerTest {

	@Test
	void preservesBearerMappingAndResponseBodies() {
		var service = mock(CourseService.class);
		var controller = new CourseController(service);
		var catalog = List.of(new CourseSummary("course", "课程", "BASICS", "v1", 1));
		var detail = new CourseDetail("course", "课程", "BASICS", "v1", List.of());
		var progress = new Progress("course", "v1", 1, 4);
		var feedback = new Feedback("objective", "task", progress);
		when(service.catalog("token")).thenReturn(catalog);
		when(service.course("token", "course")).thenReturn(detail);
		when(service.progress("token", "course")).thenReturn(progress);
		when(service.submit("token", "course", "lesson", "media", "key")).thenReturn(feedback);
		assertThat(controller.catalog("Bearer token")).isSameAs(catalog);
		assertThat(controller.course("Bearer token", "course").getBody()).isSameAs(detail);
		assertThat(controller.progress("Bearer token", "course")).isSameAs(progress);
		var response = controller.submit("Bearer token", "key", "course", "lesson", new Assignment("media"));
		assertThat(response.getStatusCode().value()).isEqualTo(201);
		assertThat(response.getBody()).isSameAs(feedback);
	}

	@Test
	void missingCourseRemainsNotFound() {
		var service = mock(CourseService.class);
		when(service.course("token", "missing")).thenThrow(new CourseNotFound());
		assertThat(new CourseController(service).course("Bearer token", "missing").getStatusCode().value())
				.isEqualTo(404);
	}

}
