package com.frameforward.equipment;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    public static final String VERSION = "p0-2026-01";
    private final EquipmentManager manager;

    public CatalogService(EquipmentManager manager) {
        this.manager = manager;
    }

    public List<Camera> cameras() {
        return manager.cameras().stream().map(Camera::from).toList();
    }

    public List<Lens> lenses(String brand) {
        return manager.lenses(brand).stream().map(Lens::from).toList();
    }

    public List<AccessoryType> accessories() {
        return manager.accessories().stream().map(AccessoryType::from).toList();
    }

    public Compatibility compatibility(String cameraId, String lensId) {
        var camera = manager.camera(cameraId);
        var lens = manager.lens(lensId);
        if (camera == null || lens == null)
            throw new CatalogNotFoundException();
        var result = CompatibilityRules.evaluate(camera.mount, camera.sensorFormat, lens.mount, lens.sensorFormat);
        return new Compatibility(camera.id, lens.id, result.compatible(), result.cropModeRequired(), result.mode());
    }

    public record Camera(String id, String brand, String model, String mount, String sensorFormat) {
        static Camera from(CameraEntity c) {
            return new Camera(c.id, c.brand, c.model, c.mount, c.sensorFormat);
        }
    }
    public record Lens(String id, String brand, String model, String mount, short focalLengthMinMm,
            short focalLengthMaxMm, BigDecimal maximumAperture, String sensorFormat) {
        static Lens from(LensEntity l) {
            return new Lens(l.id, l.brand, l.model, l.mount, l.focalLengthMinMm, l.focalLengthMaxMm, l.maximumAperture,
                    l.sensorFormat);
        }
    }
    public record AccessoryType(String id, String displayName) {
        static AccessoryType from(AccessoryTypeEntity a) {
            return new AccessoryType(a.id, a.displayName);
        }
    }
    public record Compatibility(String cameraId, String lensId, boolean compatible, boolean cropModeRequired,
            String mode) {
    }
    public static final class CatalogNotFoundException extends RuntimeException {
    }
}
