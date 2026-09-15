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

	@TableId
	public String id;

	public String accountId;

	public String environmentMediaId;

	public String subjectType;

	public String subjectText;

	public String targetStyle;

	public int timeConstraintMinutes;

	public String equipmentSnapshotJson;

	public String aiTaskId;

	public Instant createdAt;

}
