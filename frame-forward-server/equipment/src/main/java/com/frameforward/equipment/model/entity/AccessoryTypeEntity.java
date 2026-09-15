package com.frameforward.equipment.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("catalog_accessory_types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessoryTypeEntity {

	/** 配件类型唯一标识。 */
	@TableId
	public String id;

	/** 配件类型显示名称。 */
	public String displayName;

}
