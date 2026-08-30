package com.frameforward.auth;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.Instant;

@TableName("refresh_sessions")
class RefreshSessionEntity {
    @TableId String tokenHash;
    String accountId;
    Instant expiresAt;
    RefreshSessionEntity() {}
    RefreshSessionEntity(String tokenHash, String accountId, Instant expiresAt) { this.tokenHash = tokenHash; this.accountId = accountId; this.expiresAt = expiresAt; }
    String getAccountId() { return accountId; }
}
