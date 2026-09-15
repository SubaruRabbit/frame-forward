package com.frameforward.evaluation.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("retake_links")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetakeLinkEntity {

	@TableId
	public String retakeEvaluationId;

	public String originalEvaluationId, accountId, sessionId;

	public Instant createdAt;

}
