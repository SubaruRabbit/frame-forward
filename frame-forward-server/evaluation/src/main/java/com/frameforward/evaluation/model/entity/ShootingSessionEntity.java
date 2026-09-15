package com.frameforward.evaluation.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("shooting_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShootingSessionEntity {

	/** 拍摄会话唯一标识。 */
	@TableId
	public String id;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 使用的拍摄方案唯一标识。 */
	public String shootingPlanId;

	/** 拍摄方案上下文摘要。 */
	public String planContext;

	/** 拍摄会话创建时间。 */
	public Instant createdAt;

}
