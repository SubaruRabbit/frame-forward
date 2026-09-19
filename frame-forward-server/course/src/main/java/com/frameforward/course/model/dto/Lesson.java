package com.frameforward.course.model.dto;

public record Lesson(
		String id,
		String title,
		String objective,
		String content,
		String correctExample,
		String incorrectExample,
		JudgmentExercise exercise,
		String assignment) {

	public Lesson(String id, String title, String objective) {
		this(id, title, objective, "", "", "", new JudgmentExercise("", false, ""), "");
	}

}
