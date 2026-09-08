package com.frameforward.equipment.model.dto;
import com.frameforward.equipment.model.entity.UserEquipmentEntity;
public record UserEquipmentItem(String id, String kind, String catalogItemId, String nickname, boolean primary) {
    public static UserEquipmentItem from(UserEquipmentEntity item) {
        return new UserEquipmentItem(item.id, item.kind, item.catalogItemId, item.nickname, item.isPrimary);
    }
}
