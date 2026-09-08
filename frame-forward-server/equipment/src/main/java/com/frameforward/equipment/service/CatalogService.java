package com.frameforward.equipment.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.frameforward.equipment.business.CatalogNotFoundException;
import com.frameforward.equipment.business.CompatibilityRules;
import com.frameforward.equipment.manager.EquipmentManager;
import com.frameforward.equipment.model.dto.AccessoryType;
import com.frameforward.equipment.model.dto.Camera;
import com.frameforward.equipment.model.dto.Compatibility;
import com.frameforward.equipment.model.dto.Lens;

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

}
