package com.frameforward.generation.repository;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.generation.mapper.ReferenceImageMapper;
import com.frameforward.generation.model.entity.ReferenceImageEntity;
import com.frameforward.shooting.mapper.ShootingPlanMapper;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;
@Repository
public class ReferenceImageRepository {
    private final ShootingPlanMapper plans;
    private final ReferenceImageMapper references;
    public ReferenceImageRepository(ShootingPlanMapper plans, ReferenceImageMapper references) {
        this.plans = plans;
        this.references = references;
    }
    public ShootingPlanEntity findOwnedPlan(String accountId, String planId) {
        return plans.selectOne(new LambdaQueryWrapper<ShootingPlanEntity>().eq(ShootingPlanEntity::getId, planId)
                .eq(ShootingPlanEntity::getAccountId, accountId));
    }
    public ReferenceImageEntity findByTask(String taskId) {
        return references.selectOne(
                new LambdaQueryWrapper<ReferenceImageEntity>().eq(ReferenceImageEntity::getAiTaskId, taskId));
    }
    public void save(ReferenceImageEntity reference) {
        references.insert(reference);
    }
    public void update(ReferenceImageEntity reference) {
        references.updateById(reference);
    }
}
