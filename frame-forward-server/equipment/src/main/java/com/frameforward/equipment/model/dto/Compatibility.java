package com.frameforward.equipment.model.dto;
public record Compatibility(String cameraId, String lensId, boolean compatible, boolean cropModeRequired, String mode) {
}
