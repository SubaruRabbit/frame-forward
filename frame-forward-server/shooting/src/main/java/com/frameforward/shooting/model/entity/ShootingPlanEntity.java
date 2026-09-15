package com.frameforward.shooting.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("shooting_plans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShootingPlanEntity {

	@TableId
	public String id;

	public String accountId, sceneAnalysisId, aiTaskId, sceneSnapshotJson, equipmentSnapshotJson;

	public Instant createdAt;

}
