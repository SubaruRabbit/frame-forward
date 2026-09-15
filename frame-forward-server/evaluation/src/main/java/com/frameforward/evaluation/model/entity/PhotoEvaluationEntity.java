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

	@TableId
	public String id;

	public String accountId, mediaId, contentHash, ruleVersion, executionVersion, aiTaskId, resultJson, sessionId;

	public Instant createdAt;

}
