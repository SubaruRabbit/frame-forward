package com.frameforward.shooting.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("scene_analyses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneAnalysisEntity {

	/** 场景分析唯一标识。 */
	@TableId
	public String id;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 环境照片媒体唯一标识。 */
	public String environmentMediaId;

	/** 拍摄主体类型。 */
	public String subjectType;

	/** 拍摄主体描述。 */
	public String subjectText;

	/** 目标拍摄风格。 */
	public String targetStyle;

	/** 拍摄时间限制，单位为分钟。 */
	public int timeConstraintMinutes;

	/** 分析时的器材快照。 */
	public String equipmentSnapshotJson;

	/** 场景分析 AI 任务唯一标识。 */
	public String aiTaskId;

	/** 场景分析创建时间。 */
	public Instant createdAt;

}
