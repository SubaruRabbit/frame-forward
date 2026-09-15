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

	@TableId
	public String id;

	public String accountId;

	public String deletionTokenHash;

	public String state;

	public String failureReason;

	public Instant deletionTokenExpiresAt;

	public Instant createdAt;

	public Instant updatedAt;

}
