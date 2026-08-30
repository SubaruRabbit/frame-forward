package com.frameforward.equipment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    public static final String VERSION = "p0-2026-01";
    private final CameraMapper cameras;
    private final LensMapper lenses;
    private final AccessoryTypeMapper accessories;

    public CatalogService(CameraMapper cameras, LensMapper lenses, AccessoryTypeMapper accessories) {
        this.cameras = cameras; this.lenses = lenses; this.accessories = accessories;
    }

    public List<Camera> cameras() {
        return cameras.selectList(new QueryWrapper<CameraEntity>().orderByAsc("brand", "model")).stream().map(Camera::from).toList();
    }

    public List<Lens> lenses(String brand) {
        var query = new QueryWrapper<LensEntity>().orderByAsc("brand", "model");
        if (brand != null && !brand.isBlank()) query.eq("brand", brand.trim());
        return lenses.selectList(query).stream().map(Lens::from).toList();
    }

    public List<AccessoryType> accessories() {
        return accessories.selectList(new QueryWrapper<AccessoryTypeEntity>().orderByAsc("id")).stream().map(AccessoryType::from).toList();
    }

    public Compatibility compatibility(String cameraId, String lensId) {
        var camera = cameras.selectById(cameraId);
        var lens = lenses.selectById(lensId);
        if (camera == null || lens == null) throw new CatalogNotFoundException();
        var result = CompatibilityRules.evaluate(camera.mount, camera.sensorFormat, lens.mount, lens.sensorFormat);
        return new Compatibility(camera.id, lens.id, result.compatible(), result.cropModeRequired(), result.mode());
    }

    public record Camera(String id, String brand, String model, String mount, String sensorFormat) {
        static Camera from(CameraEntity c) { return new Camera(c.id, c.brand, c.model, c.mount, c.sensorFormat); }
    }
    public record Lens(String id, String brand, String model, String mount, short focalLengthMinMm, short focalLengthMaxMm, BigDecimal maximumAperture, String sensorFormat) {
        static Lens from(LensEntity l) { return new Lens(l.id, l.brand, l.model, l.mount, l.focalLengthMinMm, l.focalLengthMaxMm, l.maximumAperture, l.sensorFormat); }
    }
    public record AccessoryType(String id, String displayName) {
        static AccessoryType from(AccessoryTypeEntity a) { return new AccessoryType(a.id, a.displayName); }
    }
    public record Compatibility(String cameraId, String lensId, boolean compatible, boolean cropModeRequired, String mode) {}
    public static final class CatalogNotFoundException extends RuntimeException {}
}
