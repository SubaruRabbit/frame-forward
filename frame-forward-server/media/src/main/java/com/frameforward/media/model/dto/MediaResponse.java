package com.frameforward.media.model.dto;
public record MediaResponse(String id, int width, int height, String contentHash, String status, String exif) {
}
