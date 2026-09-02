package com.frameforward.shooting;
import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
@TableName("shooting_plans")
public class ShootingPlanEntity {
    @TableId
    public String id;
    public String accountId, sceneAnalysisId, aiTaskId, sceneSnapshotJson, equipmentSnapshotJson;
    public Instant createdAt;
    public String getId() {
        return id;
    }
    public String getAccountId() {
        return accountId;
    }
    public String getAiTaskId() {
        return aiTaskId;
    }
}
