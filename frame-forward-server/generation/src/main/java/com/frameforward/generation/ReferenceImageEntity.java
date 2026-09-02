package com.frameforward.generation;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("reference_images")
public class ReferenceImageEntity {
    @TableId
    public String id;
    public String accountId, environmentMediaId, shootingPlanId, aiTaskId, selectedPlanLabel, promptText,
            generatedMediaId;
    public Instant createdAt;
    public String getAiTaskId() {
        return aiTaskId;
    }
}
