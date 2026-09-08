package com.frameforward.equipment.model.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("catalog_lenses")
public class LensEntity {
    @TableId
    public String id;
    public String brand;
    public String model;
    public String mount;
    public short focalLengthMinMm;
    public short focalLengthMaxMm;
    public BigDecimal maximumAperture;
    public String sensorFormat;
}
