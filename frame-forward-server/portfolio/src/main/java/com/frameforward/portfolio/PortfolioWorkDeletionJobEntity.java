package com.frameforward.portfolio;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.Instant;

@TableName("portfolio_work_deletion_jobs")
public class PortfolioWorkDeletionJobEntity {
  @TableId public String id;
  public String accountId;
  public String mediaId;
  public String state;
  public String failureReason;
  public Instant createdAt;
  public Instant updatedAt;
  public String getId() { return id; }
  public String getAccountId() { return accountId; }
  public String getMediaId() { return mediaId; }
  public String getState() { return state; }
}
