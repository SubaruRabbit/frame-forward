package com.frameforward.evaluation;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.auth.AuthService;
import com.frameforward.shooting.ShootingPlanEntity;
import com.frameforward.shooting.ShootingPlanMapper;

@Service
public class ShootingSessionService {
    private final AuthService auth;
    private final ShootingPlanMapper plans;
    private final ShootingSessionMapper sessions;
    public ShootingSessionService(AuthService auth, ShootingPlanMapper plans, ShootingSessionMapper sessions) {
        this.auth = auth;
        this.plans = plans;
        this.sessions = sessions;
    }
    @Transactional
    public ShootingSessionEntity create(String token, Request request) {
        if (request == null || blank(request.shootingPlanId) || blank(request.planContext))
            throw new Invalid();
        String account = auth.requireAccountId(token);
        ShootingPlanEntity plan = plans.selectOne(new LambdaQueryWrapper<ShootingPlanEntity>()
                .eq(ShootingPlanEntity::getId, request.shootingPlanId).eq(ShootingPlanEntity::getAccountId, account));
        if (plan == null)
            throw new NotFound();
        ShootingSessionEntity session = new ShootingSessionEntity();
        session.id = UUID.randomUUID().toString();
        session.accountId = account;
        session.shootingPlanId = plan.id;
        session.planContext = request.planContext;
        session.createdAt = Instant.now();
        sessions.insert(session);
        return session;
    }
    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
    public static class Request {
        public String shootingPlanId, planContext;
    }
    public static class Invalid extends RuntimeException {
    }
    public static class NotFound extends RuntimeException {
    }
}
