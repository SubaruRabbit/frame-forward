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

	/** 镜头目录唯一标识。 */
	@TableId
	public String id;

	/** 镜头品牌。 */
	public String brand;

	/** 镜头型号。 */
	public String model;

	/** 镜头卡口类型。 */
	public String mount;

	/** 最小焦距，单位为毫米。 */
	public short focalLengthMinMm;

	/** 最大焦距，单位为毫米。 */
	public short focalLengthMaxMm;

	/** 最大光圈值。 */
	public BigDecimal maximumAperture;

	/** 镜头适配的传感器画幅。 */
	public String sensorFormat;

}
