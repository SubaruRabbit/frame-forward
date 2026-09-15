package com.frameforward.generation.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("reference_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceImageEntity {

	@TableId
	public String id;

	public String accountId, environmentMediaId, shootingPlanId, aiTaskId, selectedPlanLabel, promptText,
			generatedMediaId;

	public Instant createdAt;

}
