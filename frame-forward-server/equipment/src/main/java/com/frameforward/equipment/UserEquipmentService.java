package com.frameforward.equipment;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.frameforward.equipment.CompatibilityRules.Result;

@Service
public class UserEquipmentService {
    private final UserEquipmentMapper equipment;
    private final CameraMapper cameras;
    private final LensMapper lenses;
    private final AccessoryTypeMapper accessories;

    public UserEquipmentService(UserEquipmentMapper equipment, CameraMapper cameras, LensMapper lenses,
            AccessoryTypeMapper accessories) {
        this.equipment = equipment;
        this.cameras = cameras;
        this.lenses = lenses;
        this.accessories = accessories;
    }

    public List<Item> list(String accountId) {
        return equipment
                .selectList(
                        new LambdaQueryWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getAccountId, accountId)
                                .orderByAsc(UserEquipmentEntity::getKind, UserEquipmentEntity::getCatalogItemId))
                .stream().map(Item::from).toList();
    }

    @Transactional
    public Item add(String accountId, String kind, String catalogItemId, String nickname) {
        var normalizedKind = EquipmentKind.parse(kind);
        if (!catalogExists(normalizedKind, catalogItemId))
            throw new CatalogService.CatalogNotFoundException();
        boolean firstCamera = normalizedKind == EquipmentKind.CAMERA && countCameras(accountId) == 0;
        var item = new UserEquipmentEntity(UUID.randomUUID().toString(), accountId, normalizedKind.name(),
                catalogItemId, nickname, firstCamera);
        try {
            equipment.insert(item);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateEquipmentException();
        }
        return Item.from(item);
    }

    public void remove(String accountId, String id) {
        if (equipment.delete(new LambdaQueryWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getId, id)
                .eq(UserEquipmentEntity::getAccountId, accountId)) != 1) {
            throw new EquipmentNotFoundException();
        }
    }

    @Transactional
    public Item setPrimaryCamera(String accountId, String id) {
        var camera = owned(accountId, id);
        if (!EquipmentKind.CAMERA.name().equals(camera.kind))
            throw new NotCameraException();
        equipment.update(new LambdaUpdateWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getAccountId, accountId)
                .eq(UserEquipmentEntity::getKind, EquipmentKind.CAMERA.name())
                .set(UserEquipmentEntity::getIsPrimary, false));
        equipment.update(new LambdaUpdateWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getId, id)
                .eq(UserEquipmentEntity::getAccountId, accountId).set(UserEquipmentEntity::getIsPrimary, true));
        camera.isPrimary = true;
        return Item.from(camera);
    }

    public List<BodyLensCombination> combinations(String accountId) {
        var owned = equipment.selectList(
                new LambdaQueryWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getAccountId, accountId));
        var ownedCameras = owned.stream().filter(item -> EquipmentKind.CAMERA.name().equals(item.kind)).toList();
        var ownedLenses = owned.stream().filter(item -> EquipmentKind.LENS.name().equals(item.kind)).toList();
        return ownedCameras.stream().flatMap(camera -> ownedLenses.stream().map(lens -> combination(camera, lens)))
                .toList();
    }

    private BodyLensCombination combination(UserEquipmentEntity cameraEquipment, UserEquipmentEntity lensEquipment) {
        var camera = cameras.selectById(cameraEquipment.catalogItemId);
        var lens = lenses.selectById(lensEquipment.catalogItemId);
        Result result = CompatibilityRules.evaluate(camera.mount, camera.sensorFormat, lens.mount, lens.sensorFormat);
        return new BodyLensCombination(cameraEquipment.id, lensEquipment.id, result.compatible(),
                result.cropModeRequired(), result.mode(), result.compatible());
    }

    private UserEquipmentEntity owned(String accountId, String id) {
        var item = equipment.selectOne(new LambdaQueryWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getId, id)
                .eq(UserEquipmentEntity::getAccountId, accountId));
        if (item == null)
            throw new EquipmentNotFoundException();
        return item;
    }

    private int countCameras(String accountId) {
        return Math.toIntExact(equipment.selectCount(
                new LambdaQueryWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getAccountId, accountId)
                        .eq(UserEquipmentEntity::getKind, EquipmentKind.CAMERA.name())));
    }

    private boolean catalogExists(EquipmentKind kind, String id) {
        return switch (kind) {
            case CAMERA -> cameras.selectById(id) != null;
            case LENS -> lenses.selectById(id) != null;
            case ACCESSORY -> accessories.selectById(id) != null;
        };
    }

    enum EquipmentKind {
        CAMERA, LENS, ACCESSORY;
        static EquipmentKind parse(String value) {
            try {
                return EquipmentKind.valueOf(value);
            } catch (Exception exception) {
                throw new InvalidEquipmentException();
            }
        }
    }

    public record Item(String id, String kind, String catalogItemId, String nickname, boolean primary) {
        static Item from(UserEquipmentEntity item) {
            return new Item(item.id, item.kind, item.catalogItemId, item.nickname, item.isPrimary);
        }
    }
    public record BodyLensCombination(String cameraEquipmentId, String lensEquipmentId, boolean compatible,
            boolean cropModeRequired, String mode, boolean defaultEligible) {
    }
    public static class EquipmentNotFoundException extends RuntimeException {
    }
    public static class DuplicateEquipmentException extends RuntimeException {
    }
    public static class NotCameraException extends RuntimeException {
    }
    public static class InvalidEquipmentException extends RuntimeException {
    }
}
