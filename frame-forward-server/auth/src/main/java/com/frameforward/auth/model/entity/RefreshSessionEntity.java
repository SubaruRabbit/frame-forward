package com.frameforward.auth.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("refresh_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshSessionEntity {

	/** 刷新令牌哈希值。 */
	@TableId
	public String tokenHash;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 会话过期时间。 */
	public Instant expiresAt;

}
