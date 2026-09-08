package com.frameforward.equipment.model.dto;
public record BodyLensCombination(String cameraEquipmentId, String lensEquipmentId, boolean compatible,
        boolean cropModeRequired, String mode, boolean defaultEligible) {
}
