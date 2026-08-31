package com.frameforward.auth;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.Instant;

@TableName("account_deletion_jobs")
class AccountDeletionJobEntity {
  @TableId String id;
  String accountId;
  String deletionTokenHash;
  String state;
  String failureReason;
  Instant deletionTokenExpiresAt;
  Instant createdAt;
  Instant updatedAt;
  String getAccountId() { return accountId; }
  String getState() { return state; }
}
