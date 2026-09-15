package com.frameforward.equipment.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("user_equipment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEquipmentEntity {

	@TableId
	public String id;

	public String accountId;

	public String kind;

	public String catalogItemId;

	public String nickname;

	public boolean isPrimary;

	public boolean getIsPrimary() {
		return isPrimary;
	}

}
