package com.frameforward.course.model.dto;
public record ContentVersion(String id, String courseId, String contentVersion, String modelId, String promptVersion,
        String sourceMaterialVersion) {
}
