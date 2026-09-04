package com.frameforward.equipment;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

/** 用户器材的归属、主力机和兼容性规则。 */
@Component
public class UserEquipmentBusiness {
    private final EquipmentManager manager;
    public UserEquipmentBusiness(EquipmentManager manager) {
        this.manager = manager;
    }
    public List<UserEquipmentService.Item> list(String accountId) {
        return manager
                .owned(accountId).stream().sorted(java.util.Comparator
                        .comparing((UserEquipmentEntity item) -> item.kind).thenComparing(item -> item.catalogItemId))
                .map(UserEquipmentService.Item::from).toList();
    }
    public UserEquipmentService.Item add(String accountId, String kind, String catalogItemId, String nickname) {
        String normalized = parse(kind);
        if (!manager.catalogExists(normalized, catalogItemId))
            throw new CatalogService.CatalogNotFoundException();
        var item = new UserEquipmentEntity(UUID.randomUUID().toString(), accountId, normalized, catalogItemId, nickname,
                "CAMERA".equals(normalized) && manager.count(accountId, normalized) == 0);
        try {
            manager.add(item);
        } catch (DuplicateKeyException exception) {
            throw new UserEquipmentService.DuplicateEquipmentException();
        }
        return UserEquipmentService.Item.from(item);
    }
    public void remove(String accountId, String id) {
        if (!manager.delete(accountId, id))
            throw new UserEquipmentService.EquipmentNotFoundException();
    }
    public UserEquipmentService.Item setPrimary(String accountId, String id) {
        var item = manager.owned(accountId, id);
        if (item == null)
            throw new UserEquipmentService.EquipmentNotFoundException();
        if (!"CAMERA".equals(item.kind))
            throw new UserEquipmentService.NotCameraException();
        manager.clearPrimary(accountId);
        manager.setPrimary(accountId, id);
        item.isPrimary = true;
        return UserEquipmentService.Item.from(item);
    }
    public List<UserEquipmentService.BodyLensCombination> combinations(String accountId) {
        var all = manager.owned(accountId);
        return all.stream().filter(item -> "CAMERA".equals(item.kind)).flatMap(
                camera -> all.stream().filter(item -> "LENS".equals(item.kind)).map(lens -> combination(camera, lens)))
                .toList();
    }
    private UserEquipmentService.BodyLensCombination combination(UserEquipmentEntity cameraItem,
            UserEquipmentEntity lensItem) {
        var camera = manager.camera(cameraItem.catalogItemId);
        var lens = manager.lens(lensItem.catalogItemId);
        var result = CompatibilityRules.evaluate(camera.mount, camera.sensorFormat, lens.mount, lens.sensorFormat);
        return new UserEquipmentService.BodyLensCombination(cameraItem.id, lensItem.id, result.compatible(),
                result.cropModeRequired(), result.mode(), result.compatible());
    }
    private static String parse(String kind) {
        try {
            return UserEquipmentService.EquipmentKind.valueOf(kind).name();
        } catch (Exception exception) {
            throw new UserEquipmentService.InvalidEquipmentException();
        }
    }
}
