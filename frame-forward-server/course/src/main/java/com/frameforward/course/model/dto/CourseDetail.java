package com.frameforward.course.model.dto;

import java.util.List;

public record CourseDetail(
		String id,
		String title,
		String category,
		String contentVersion,
		List<Chapter> chapters) {

	public CourseDetail {
		chapters = List.copyOf(chapters);
	}

	public List<Lesson> lessons() {
		return chapters.stream().flatMap(chapter -> chapter.lessons().stream()).toList();
	}

}
