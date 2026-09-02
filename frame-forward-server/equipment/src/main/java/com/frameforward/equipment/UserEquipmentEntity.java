package com.frameforward.equipment;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("user_equipment")
public class UserEquipmentEntity {
    @TableId
    public String id;
    public String accountId;
    public String kind;
    public String catalogItemId;
    public String nickname;
    public boolean isPrimary;

    public UserEquipmentEntity() {
    }

    UserEquipmentEntity(String id, String accountId, String kind, String catalogItemId, String nickname,
            boolean isPrimary) {
        this.id = id;
        this.accountId = accountId;
        this.kind = kind;
        this.catalogItemId = catalogItemId;
        this.nickname = nickname;
        this.isPrimary = isPrimary;
    }

    public String getAccountId() {
        return accountId;
    }
    public String getId() {
        return id;
    }
    public String getKind() {
        return kind;
    }
    public String getCatalogItemId() {
        return catalogItemId;
    }
    public boolean getIsPrimary() {
        return isPrimary;
    }
    public boolean isPrimary() {
        return isPrimary;
    }
}
