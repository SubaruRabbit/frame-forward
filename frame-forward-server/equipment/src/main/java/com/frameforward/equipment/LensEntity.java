package com.frameforward.equipment;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("catalog_lenses")
public class LensEntity {
    @TableId public String id;
    public String brand;
    public String model;
    public String mount;
    public short focalLengthMinMm;
    public short focalLengthMaxMm;
    public BigDecimal maximumAperture;
    public String sensorFormat;
}
