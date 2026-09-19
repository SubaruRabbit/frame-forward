package com.frameforward.course.model.dto;

import java.util.List;

public record Chapter(
		String id,
		String title,
		int sequence,
		List<Lesson> lessons) {

	public Chapter {
		lessons = List.copyOf(lessons);
	}

}
