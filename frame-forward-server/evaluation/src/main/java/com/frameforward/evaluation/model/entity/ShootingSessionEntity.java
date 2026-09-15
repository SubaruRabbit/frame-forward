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

	@TableId
	public String id;

	public String accountId, shootingPlanId, planContext;

	public Instant createdAt;

}
