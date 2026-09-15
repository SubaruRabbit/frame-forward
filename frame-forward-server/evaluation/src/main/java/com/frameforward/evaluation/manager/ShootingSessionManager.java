package com.frameforward.evaluation.manager;

import org.springframework.stereotype.Component;

import com.frameforward.evaluation.model.entity.ShootingSessionEntity;
import com.frameforward.evaluation.repository.ShootingSessionRepository;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;

@Component
@lombok.RequiredArgsConstructor
public class ShootingSessionManager {

	private final ShootingSessionRepository repository;

	public ShootingPlanEntity findOwnedPlan(String accountId, String planId) {
		return repository.findOwnedPlan(accountId, planId);
	}

	public void save(ShootingSessionEntity session) {
		repository.save(session);
	}

}
