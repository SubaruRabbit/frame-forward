package com.frameforward.evaluation.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("photo_evaluations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoEvaluationEntity {

	/** 照片评估唯一标识。 */
	@TableId
	public String id;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 被评估媒体唯一标识。 */
	public String mediaId;

	/** 被评估媒体内容哈希值。 */
	public String contentHash;

	/** 使用的评估规则版本。 */
	public String ruleVersion;

	/** 评估执行版本。 */
	public String executionVersion;

	/** 评估 AI 任务唯一标识。 */
	public String aiTaskId;

	/** 照片评估结果。 */
	public String resultJson;

	/** 所属拍摄会话唯一标识。 */
	public String sessionId;

	/** 照片评估创建时间。 */
	public Instant createdAt;

}
