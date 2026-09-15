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

	/** 参考图记录唯一标识。 */
	@TableId
	public String id;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 输入环境照片媒体唯一标识。 */
	public String environmentMediaId;

	/** 关联拍摄方案唯一标识。 */
	public String shootingPlanId;

	/** 参考图生成 AI 任务唯一标识。 */
	public String aiTaskId;

	/** 用户选择的方案标签。 */
	public String selectedPlanLabel;

	/** 参考图生成提示词。 */
	public String promptText;

	/** 生成结果媒体唯一标识。 */
	public String generatedMediaId;

	/** 参考图记录创建时间。 */
	public Instant createdAt;

}
