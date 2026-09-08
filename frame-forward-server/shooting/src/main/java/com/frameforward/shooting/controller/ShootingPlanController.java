package com.frameforward.shooting.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.auth.service.AuthService;
import com.frameforward.shooting.model.dto.ShootingPlanRequest;
import com.frameforward.shooting.service.ShootingPlanService;
@RestController
@RequestMapping("/shooting-plans")
public class ShootingPlanController {
    private final ShootingPlanService plans;
    public ShootingPlanController(ShootingPlanService plans) {
        this.plans = plans;
    }
    @PostMapping
    ResponseEntity<AiTaskCreated> create(@RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @RequestBody ShootingPlanRequest request) {
        return ResponseEntity.accepted().body(plans.create(AuthService.bearer(authorization), key, request));
    }
}
