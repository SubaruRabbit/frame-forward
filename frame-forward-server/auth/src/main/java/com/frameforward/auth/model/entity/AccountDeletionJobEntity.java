package com.frameforward.auth.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("account_deletion_jobs")
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
    public String getAccountId() {
        return accountId;
    }
    public String getState() {
        return state;
    }
}
