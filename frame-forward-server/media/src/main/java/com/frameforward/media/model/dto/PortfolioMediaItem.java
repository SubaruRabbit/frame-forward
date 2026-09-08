package com.frameforward.media.model.dto;
import java.util.Map;
public record PortfolioMediaItem(String mediaId, int width, int height, Map<String, String> exif) {
}
