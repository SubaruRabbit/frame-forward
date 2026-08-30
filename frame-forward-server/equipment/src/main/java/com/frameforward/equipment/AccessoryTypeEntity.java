package com.frameforward.equipment;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("catalog_accessory_types")
public class AccessoryTypeEntity {
    @TableId public String id;
    public String displayName;
}
