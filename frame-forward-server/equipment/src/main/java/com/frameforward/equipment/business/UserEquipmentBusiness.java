package com.frameforward.equipment.business;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.frameforward.equipment.manager.EquipmentManager;
import com.frameforward.equipment.model.EquipmentKind;
import com.frameforward.equipment.model.dto.BodyLensCombination;
import com.frameforward.equipment.model.dto.UserEquipmentItem;
import com.frameforward.equipment.model.entity.UserEquipmentEntity;

/** 用户器材的归属、主力机和兼容性规则。 */
@Component
public class UserEquipmentBusiness {
    private final EquipmentManager manager;
    public UserEquipmentBusiness(EquipmentManager manager) {
        this.manager = manager;
    }
    public List<UserEquipmentItem> list(String accountId) {
        return manager
                .owned(accountId).stream().sorted(java.util.Comparator
                        .comparing((UserEquipmentEntity item) -> item.kind).thenComparing(item -> item.catalogItemId))
                .map(UserEquipmentItem::from).toList();
    }
    public UserEquipmentItem add(String accountId, String kind, String catalogItemId, String nickname) {
        String normalized = parse(kind);
        if (!catalogExists(normalized, catalogItemId))
            throw new CatalogNotFoundException();
        var item = new UserEquipmentEntity(UUID.randomUUID().toString(), accountId, normalized, catalogItemId, nickname,
                "CAMERA".equals(normalized) && manager.count(accountId, normalized) == 0);
        try {
            manager.add(item);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateEquipmentException();
        }
        return UserEquipmentItem.from(item);
    }
    public void remove(String accountId, String id) {
        if (!manager.delete(accountId, id))
            throw new EquipmentNotFoundException();
    }
    public UserEquipmentItem setPrimary(String accountId, String id) {
        var item = manager.owned(accountId, id);
        if (item == null)
            throw new EquipmentNotFoundException();
        if (!"CAMERA".equals(item.kind))
            throw new NotCameraException();
        manager.setPrimary(accountId, id);
        item.isPrimary = true;
        return UserEquipmentItem.from(item);
    }
    public List<BodyLensCombination> combinations(String accountId) {
        var all = manager.owned(accountId);
        return all.stream().filter(item -> "CAMERA".equals(item.kind)).flatMap(
                camera -> all.stream().filter(item -> "LENS".equals(item.kind)).map(lens -> combination(camera, lens)))
                .toList();
    }
    private BodyLensCombination combination(UserEquipmentEntity cameraItem, UserEquipmentEntity lensItem) {
        var camera = manager.camera(cameraItem.catalogItemId);
        var lens = manager.lens(lensItem.catalogItemId);
        var result = CompatibilityRules.evaluate(camera.mount, camera.sensorFormat, lens.mount, lens.sensorFormat);
        return new BodyLensCombination(cameraItem.id, lensItem.id, result.compatible(), result.cropModeRequired(),
                result.mode(), result.compatible());
    }
    private boolean catalogExists(String kind, String id) {
        return switch (kind) {
            case "CAMERA" -> manager.camera(id) != null;
            case "LENS" -> manager.lens(id) != null;
            case "ACCESSORY" -> manager.accessory(id) != null;
            default -> false;
        };
    }
    private static String parse(String kind) {
        try {
            return EquipmentKind.valueOf(kind).name();
        } catch (Exception exception) {
            throw new InvalidEquipmentException();
        }
    }
}
