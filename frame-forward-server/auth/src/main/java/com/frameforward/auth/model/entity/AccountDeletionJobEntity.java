package com.frameforward.auth.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("account_deletion_jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDeletionJobEntity {

	/** 删除任务唯一标识。 */
	@TableId
	public String id;

	/** 待删除账户唯一标识。 */
	public String accountId;

	/** 删除确认令牌哈希值。 */
	public String deletionTokenHash;

	/** 删除任务状态。 */
	public String state;

	/** 删除失败原因。 */
	public String failureReason;

	/** 删除确认令牌过期时间。 */
	public Instant deletionTokenExpiresAt;

	/** 删除任务创建时间。 */
	public Instant createdAt;

	/** 删除任务更新时间。 */
	public Instant updatedAt;

}
