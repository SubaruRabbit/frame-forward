package com.frameforward.ai.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("ai_tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiTaskEntity {

	/** AI 任务唯一标识。 */
	@TableId
	public String id;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** AI 任务操作类型。 */
	public String operationType;

	/** 客户端幂等键。 */
	public String idempotencyKey;

	/** AI 任务状态。 */
	public String state;

	/** 工作流版本。 */
	public String workflowVersion;

	/** 使用的模型标识。 */
	public String modelId;

	/** 提示词版本。 */
	public String promptVersion;

	/** 业务规则版本。 */
	public String ruleVersion;

	/** 输入输出结构版本。 */
	public String schemaVersion;

	/** 任务输入数据。 */
	public String inputJson;

	/** 任务结果数据。 */
	public String resultJson;

	/** 任务失败错误码。 */
	public String errorCode;

	/** 任务创建时间。 */
	public Instant createdAt;

	/** 任务更新时间。 */
	public Instant updatedAt;

}
