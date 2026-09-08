package com.frameforward.course.model.dto;
public record Progress(String courseId, String contentVersion, int completedLessons, int totalLessons) {
}
