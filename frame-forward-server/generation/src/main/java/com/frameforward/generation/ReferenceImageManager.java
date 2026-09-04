package com.frameforward.generation;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.ai.AiTaskEntity;
import com.frameforward.media.MediaService;
import com.frameforward.shooting.ShootingPlanEntity;
import com.frameforward.shooting.ShootingPlanMapper;

@Component
class ReferenceImageManager {
    private final ShootingPlanMapper plans;
    private final ReferenceImageMapper references;
    private final MediaService media;

    ReferenceImageManager(ShootingPlanMapper plans, ReferenceImageMapper references, MediaService media) {
        this.plans = plans;
        this.references = references;
        this.media = media;
    }

    ShootingPlanEntity findOwnedPlan(String accountId, String planId) {
        return plans.selectOne(new LambdaQueryWrapper<ShootingPlanEntity>().eq(ShootingPlanEntity::getId, planId)
                .eq(ShootingPlanEntity::getAccountId, accountId));
    }

    void persistReferenceIfAbsent(NewReference source) {
        if (references.selectOne(new LambdaQueryWrapper<ReferenceImageEntity>().eq(ReferenceImageEntity::getAiTaskId,
                source.aiTaskId())) != null) {
            return;
        }
        ReferenceImageEntity reference = new ReferenceImageEntity();
        reference.id = UUID.randomUUID().toString();
        reference.accountId = source.accountId();
        reference.environmentMediaId = source.environmentMediaId();
        reference.shootingPlanId = source.shootingPlanId();
        reference.aiTaskId = source.aiTaskId();
        reference.selectedPlanLabel = source.selectedPlanLabel();
        reference.promptText = source.promptText();
        reference.createdAt = Instant.now();
        references.insert(reference);
    }

    void complete(AiTaskEntity task, Map<String, Object> result) {
        ReferenceImageEntity reference = references.selectOne(
                new LambdaQueryWrapper<ReferenceImageEntity>().eq(ReferenceImageEntity::getAiTaskId, task.id));
        if (reference == null || reference.generatedMediaId != null) {
            return;
        }
        var generated = media.registerGenerated(task.accountId, String.valueOf(result.get("imageUrl")),
                ((Number) result.get("width")).intValue(), ((Number) result.get("height")).intValue());
        reference.generatedMediaId = generated.id();
        references.updateById(reference);
        result.put("referenceImageId", reference.id);
        result.put("mediaId", generated.id());
    }

    record NewReference(String accountId, String environmentMediaId, String shootingPlanId, String aiTaskId,
            String selectedPlanLabel, String promptText) {
    }
}
