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

	@TableId
	public String id;

	public String brand;

	public String model;

	public String mount;

	public String sensorFormat;

}
