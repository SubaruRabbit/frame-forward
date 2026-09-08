package com.frameforward.evaluation.service;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.auth.service.AuthService;
import com.frameforward.evaluation.business.ShootingSessionInvalid;
import com.frameforward.evaluation.business.ShootingSessionNotFound;
import com.frameforward.evaluation.manager.ShootingSessionManager;
import com.frameforward.evaluation.model.dto.ShootingSessionRequest;
import com.frameforward.evaluation.model.entity.ShootingSessionEntity;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;

@Service
public class ShootingSessionService {
    private final AuthService auth;
    private final ShootingSessionManager manager;
    public ShootingSessionService(AuthService auth, ShootingSessionManager manager) {
        this.auth = auth;
        this.manager = manager;
    }
    @Transactional
    public ShootingSessionEntity create(String token, ShootingSessionRequest request) {
        if (request == null || blank(request.shootingPlanId) || blank(request.planContext))
            throw new ShootingSessionInvalid();
        String account = auth.requireAccountId(token);
        ShootingPlanEntity plan = manager.findOwnedPlan(account, request.shootingPlanId);
        if (plan == null)
            throw new ShootingSessionNotFound();
        ShootingSessionEntity session = new ShootingSessionEntity();
        session.id = UUID.randomUUID().toString();
        session.accountId = account;
        session.shootingPlanId = plan.id;
        session.planContext = request.planContext;
        session.createdAt = Instant.now();
        manager.save(session);
        return session;
    }
    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

}
