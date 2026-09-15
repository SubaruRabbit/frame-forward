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

	/** 拍摄方案唯一标识。 */
	@TableId
	public String id;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 场景分析唯一标识。 */
	public String sceneAnalysisId;

	/** 方案生成 AI 任务唯一标识。 */
	public String aiTaskId;

	/** 生成方案使用的场景快照。 */
	public String sceneSnapshotJson;

	/** 生成方案使用的器材快照。 */
	public String equipmentSnapshotJson;

	/** 拍摄方案创建时间。 */
	public Instant createdAt;

}
