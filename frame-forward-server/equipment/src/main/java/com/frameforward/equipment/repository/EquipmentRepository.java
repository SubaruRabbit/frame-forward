package com.frameforward.equipment.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.frameforward.equipment.mapper.AccessoryTypeMapper;
import com.frameforward.equipment.mapper.CameraMapper;
import com.frameforward.equipment.mapper.LensMapper;
import com.frameforward.equipment.mapper.UserEquipmentMapper;
import com.frameforward.equipment.model.entity.AccessoryTypeEntity;
import com.frameforward.equipment.model.entity.CameraEntity;
import com.frameforward.equipment.model.entity.LensEntity;
import com.frameforward.equipment.model.entity.UserEquipmentEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 集中器材目录和用户器材的持久化访问。
 */
@Repository
public class EquipmentRepository {
    private final UserEquipmentMapper equipment;
    private final CameraMapper cameras;
    private final LensMapper lenses;
    private final AccessoryTypeMapper accessories;

    public EquipmentRepository(UserEquipmentMapper equipment, CameraMapper cameras, LensMapper lenses,
                               AccessoryTypeMapper accessories) {
        this.equipment = equipment;
        this.cameras = cameras;
        this.lenses = lenses;
        this.accessories = accessories;
    }

    public List<CameraEntity> cameras() {
        return cameras.selectList(new QueryWrapper<CameraEntity>().orderByAsc("brand", "model"));
    }

    public List<LensEntity> lenses(String brand) {
        var query = new QueryWrapper<LensEntity>().orderByAsc("brand", "model");
        if (brand != null && !brand.isBlank())
            query.eq("brand", brand.trim());
        return lenses.selectList(query);
    }

    public List<AccessoryTypeEntity> accessories() {
        return accessories.selectList(new QueryWrapper<AccessoryTypeEntity>().orderByAsc("id"));
    }

    public CameraEntity camera(String id) {
        return cameras.selectById(id);
    }

    public LensEntity lens(String id) {
        return lenses.selectById(id);
    }

    public AccessoryTypeEntity accessory(String id) {
        return accessories.selectById(id);
    }

    public List<UserEquipmentEntity> owned(String accountId) {
        return equipment.selectList(
                new LambdaQueryWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getAccountId, accountId));
    }

    public UserEquipmentEntity owned(String accountId, String id) {
        return equipment.selectOne(new LambdaQueryWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getId, id)
                .eq(UserEquipmentEntity::getAccountId, accountId));
    }

    public long count(String accountId, String kind) {
        return equipment.selectCount(new LambdaQueryWrapper<UserEquipmentEntity>()
                .eq(UserEquipmentEntity::getAccountId, accountId).eq(UserEquipmentEntity::getKind, kind));
    }

    public void add(UserEquipmentEntity item) {
        equipment.insert(item);
    }

    public boolean delete(String accountId, String id) {
        return equipment.delete(new LambdaQueryWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getId, id)
                .eq(UserEquipmentEntity::getAccountId, accountId)) == 1;
    }

    public void clearPrimary(String accountId) {
        equipment.update(new LambdaUpdateWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getAccountId, accountId)
                .eq(UserEquipmentEntity::getKind, "CAMERA").set(UserEquipmentEntity::getIsPrimary, false));
    }

    public void setPrimary(String accountId, String id) {
        equipment.update(new LambdaUpdateWrapper<UserEquipmentEntity>().eq(UserEquipmentEntity::getId, id)
                .eq(UserEquipmentEntity::getAccountId, accountId).set(UserEquipmentEntity::getIsPrimary, true));
    }
}
