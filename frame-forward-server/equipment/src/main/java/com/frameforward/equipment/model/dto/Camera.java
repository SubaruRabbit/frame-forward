package com.frameforward.equipment.model.dto;
import com.frameforward.equipment.model.entity.CameraEntity;
public record Camera(String id, String brand, String model, String mount, String sensorFormat) {
    public static Camera from(CameraEntity c) {
        return new Camera(c.id, c.brand, c.model, c.mount, c.sensorFormat);
    }
}
