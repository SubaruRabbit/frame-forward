package com.frameforward.equipment.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("catalog_cameras")
public class CameraEntity {
    @TableId
    public String id;
    public String brand;
    public String model;
    public String mount;
    public String sensorFormat;
}
