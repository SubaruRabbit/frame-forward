package com.frameforward.evaluation;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("shooting_sessions")
public class ShootingSessionEntity {
    @TableId
    public String id;
    public String accountId, shootingPlanId, planContext;
    public Instant createdAt;
    public String getId() {
        return id;
    }
    public String getAccountId() {
        return accountId;
    }
    public String getShootingPlanId() {
        return shootingPlanId;
    }
}
