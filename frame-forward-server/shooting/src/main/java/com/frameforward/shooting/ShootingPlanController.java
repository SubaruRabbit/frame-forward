package com.frameforward.shooting;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthController;
@RestController
@RequestMapping("/shooting-plans")
public class ShootingPlanController {
    private final ShootingPlanService plans;
    public ShootingPlanController(ShootingPlanService plans) {
        this.plans = plans;
    }
    @PostMapping
    ResponseEntity<AiTaskRuntime.Created> create(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @RequestBody ShootingPlanService.Request request) {
        return ResponseEntity.accepted().body(plans.create(AuthController.bearer(authorization), key, request));
    }
}
