package com.frameforward.equipment.model.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("catalog_lenses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
