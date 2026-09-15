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

	/** 用户器材唯一标识。 */
	@TableId
	public String id;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 器材种类。 */
	public String kind;

	/** 关联的器材目录标识。 */
	public String catalogItemId;

	/** 用户自定义器材昵称。 */
	public String nickname;

	/** 是否为主相机。 */
	public boolean isPrimary;

	public boolean getIsPrimary() {
		return isPrimary;
	}

}
