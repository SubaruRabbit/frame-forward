package com.frameforward.equipment.model.dto;
import com.frameforward.equipment.model.entity.AccessoryTypeEntity;
public record AccessoryType(String id, String displayName) {
    public static AccessoryType from(AccessoryTypeEntity a) {
        return new AccessoryType(a.id, a.displayName);
    }
}
