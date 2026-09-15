package com.frameforward.shooting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.service.AiTaskRuntime;
import com.frameforward.auth.service.AuthService;
import com.frameforward.shooting.business.ShootingBusiness;
import com.frameforward.shooting.model.dto.ShootingPlanRequest;
import com.frameforward.shooting.model.entity.SceneAnalysisEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShootingPlanService {

	private final AuthService auth;

	private final AiTaskRuntime tasks;

	private final ShootingBusiness business;

	@Transactional
	public AiTaskCreated create(String token, String key, ShootingPlanRequest request) {
		business.validate(request);
		String account = auth.requireAccountId(token);
		SceneAnalysisEntity scene = business.findOwnedScene(account, request.sceneAnalysisId);
		AiTaskCreateRequest taskRequest = AiTaskCreateRequest.builder().operationType("shooting-plan-generation")
				.input(business.planInput(scene, request)).build();
		AiTaskCreated task = tasks.create(token, key, taskRequest);
		business.persistPlanIfAbsent(account, scene, task.taskId());
		return task;
	}

}
