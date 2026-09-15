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

	/** 重拍照片评估唯一标识。 */
	@TableId
	public String retakeEvaluationId;

	/** 原始照片评估唯一标识。 */
	public String originalEvaluationId;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 所属拍摄会话唯一标识。 */
	public String sessionId;

	/** 重拍关联创建时间。 */
	public Instant createdAt;

}
