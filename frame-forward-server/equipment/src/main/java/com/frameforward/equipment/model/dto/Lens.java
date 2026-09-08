package com.frameforward.equipment.model.dto;
import java.math.BigDecimal;

import com.frameforward.equipment.model.entity.LensEntity;
public record Lens(String id, String brand, String model, String mount, short focalLengthMinMm, short focalLengthMaxMm,
        BigDecimal maximumAperture, String sensorFormat) {
    public static Lens from(LensEntity l) {
        return new Lens(l.id, l.brand, l.model, l.mount, l.focalLengthMinMm, l.focalLengthMaxMm, l.maximumAperture,
                l.sensorFormat);
    }
}
