package com.frameforward.auth.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("refresh_sessions")
public class RefreshSessionEntity {
    @TableId
    public String tokenHash;
    public String accountId;
    public Instant expiresAt;
    public RefreshSessionEntity() {
    }
    public RefreshSessionEntity(String tokenHash, String accountId, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.accountId = accountId;
        this.expiresAt = expiresAt;
    }
    public String getAccountId() {
        return accountId;
    }
}
