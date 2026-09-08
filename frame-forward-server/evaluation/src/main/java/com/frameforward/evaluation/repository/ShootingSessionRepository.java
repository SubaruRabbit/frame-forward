package com.frameforward.evaluation.repository;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.evaluation.mapper.ShootingSessionMapper;
import com.frameforward.evaluation.model.entity.ShootingSessionEntity;
import com.frameforward.shooting.mapper.ShootingPlanMapper;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;
@Repository
public class ShootingSessionRepository {
    private final ShootingPlanMapper plans;
    private final ShootingSessionMapper sessions;
    public ShootingSessionRepository(ShootingPlanMapper plans, ShootingSessionMapper sessions) {
        this.plans = plans;
        this.sessions = sessions;
    }
    public ShootingPlanEntity findOwnedPlan(String accountId, String planId) {
        return plans.selectOne(new LambdaQueryWrapper<ShootingPlanEntity>().eq(ShootingPlanEntity::getId, planId)
                .eq(ShootingPlanEntity::getAccountId, accountId));
    }
    public void save(ShootingSessionEntity session) {
        sessions.insert(session);
    }
}
