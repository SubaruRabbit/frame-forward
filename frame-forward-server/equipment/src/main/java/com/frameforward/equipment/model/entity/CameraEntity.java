package com.frameforward.equipment.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("catalog_cameras")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CameraEntity {

	/** 相机目录唯一标识。 */
	@TableId
	public String id;

	/** 相机品牌。 */
	public String brand;

	/** 相机型号。 */
	public String model;

	/** 相机卡口类型。 */
	public String mount;

	/** 相机传感器画幅。 */
	public String sensorFormat;

}
