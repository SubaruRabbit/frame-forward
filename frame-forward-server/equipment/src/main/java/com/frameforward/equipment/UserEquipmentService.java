package com.frameforward.equipment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 用户器材用例入口。 */
@Service
public class UserEquipmentService {
    private final UserEquipmentBusiness business;
    public UserEquipmentService(UserEquipmentBusiness business) {
        this.business = business;
    }
    public List<Item> list(String accountId) {
        return business.list(accountId);
    }
    @Transactional
    public Item add(String accountId, String kind, String catalogItemId, String nickname) {
        return business.add(accountId, kind, catalogItemId, nickname);
    }
    public void remove(String accountId, String id) {
        business.remove(accountId, id);
    }
    @Transactional
    public Item setPrimaryCamera(String accountId, String id) {
        return business.setPrimary(accountId, id);
    }
    public List<BodyLensCombination> combinations(String accountId) {
        return business.combinations(accountId);
    }
    enum EquipmentKind {
        CAMERA, LENS, ACCESSORY
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
