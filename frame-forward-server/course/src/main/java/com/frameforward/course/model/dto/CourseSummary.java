package com.frameforward.course.model.dto;

public record CourseSummary(
		String id,
		String title,
		String category,
		String contentVersion,
		int lessonCount) {
}
