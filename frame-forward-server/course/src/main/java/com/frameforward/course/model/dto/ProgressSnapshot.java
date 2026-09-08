package com.frameforward.course.model.dto;
public record ProgressSnapshot(String courseId, String contentVersion, int completedLessons, int totalLessons) {
}
