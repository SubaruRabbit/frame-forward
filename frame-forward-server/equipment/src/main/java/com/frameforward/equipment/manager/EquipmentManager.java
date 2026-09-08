package com.frameforward.equipment.manager;

import com.frameforward.equipment.model.entity.AccessoryTypeEntity;
import com.frameforward.equipment.model.entity.CameraEntity;
import com.frameforward.equipment.model.entity.LensEntity;
import com.frameforward.equipment.model.entity.UserEquipmentEntity;
import com.frameforward.equipment.repository.EquipmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EquipmentManager {

    private final EquipmentRepository repository;

    public EquipmentManager(EquipmentRepository repository) {
        this.repository = repository;
    }

    public List<CameraEntity> cameras() {
        return repository.cameras();
    }

    public List<LensEntity> lenses(String brand) {
        return repository.lenses(brand);
    }

    public List<AccessoryTypeEntity> accessories() {
        return repository.accessories();
    }

    public CameraEntity camera(String id) {
        return repository.camera(id);
    }

    public LensEntity lens(String id) {
        return repository.lens(id);
    }

    public AccessoryTypeEntity accessory(String id) {
        return repository.accessory(id);
    }

    public List<UserEquipmentEntity> owned(String accountId) {
        return repository.owned(accountId);
    }

    public UserEquipmentEntity owned(String accountId, String id) {
        return repository.owned(accountId, id);
    }

    public long count(String accountId, String kind) {
        return repository.count(accountId, kind);
    }

    public void add(UserEquipmentEntity item) {
        repository.add(item);
    }

    public boolean delete(String accountId, String id) {
        return repository.delete(accountId, id);
    }

    public void setPrimary(String accountId, String id) {
        repository.clearPrimary(accountId);
        repository.setPrimary(accountId, id);
    }

}
