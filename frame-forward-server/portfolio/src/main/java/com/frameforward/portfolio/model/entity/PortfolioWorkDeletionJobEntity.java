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

	@TableId
	public String id;

	public String accountId;

	public String mediaId;

	public String state;

	public String failureReason;

	public Instant createdAt;

	public Instant updatedAt;

}
