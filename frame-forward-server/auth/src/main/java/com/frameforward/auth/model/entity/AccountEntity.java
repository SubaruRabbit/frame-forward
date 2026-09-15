package com.frameforward.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

	/** 账户唯一标识。 */
	@TableId
	public String id;

	/** 登录用户名。 */
	public String username;

	/** 登录邮箱地址。 */
	public String email;

	/** 密码哈希值。 */
	public String passwordHash;

}
