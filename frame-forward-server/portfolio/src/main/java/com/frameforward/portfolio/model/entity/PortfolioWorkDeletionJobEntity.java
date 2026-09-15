package com.frameforward.portfolio.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("portfolio_work_deletion_jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioWorkDeletionJobEntity {

	/** 删除任务唯一标识。 */
	@TableId
	public String id;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 待删除媒体唯一标识。 */
	public String mediaId;

	/** 删除任务状态。 */
	public String state;

	/** 删除失败原因。 */
	public String failureReason;

	/** 删除任务创建时间。 */
	public Instant createdAt;

	/** 删除任务更新时间。 */
	public Instant updatedAt;

}
