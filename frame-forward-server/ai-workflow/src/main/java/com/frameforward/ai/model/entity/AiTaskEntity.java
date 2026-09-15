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

	@TableId
	public String id;

	public String accountId, operationType, idempotencyKey, state, workflowVersion, modelId, promptVersion, ruleVersion,
			schemaVersion, inputJson, resultJson, errorCode;

	public Instant createdAt, updatedAt;

}
