package com.frameforward.evaluation.controller;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.frameforward.auth.service.AuthService;
import com.frameforward.evaluation.model.dto.ShootingSessionRequest;
import com.frameforward.evaluation.model.entity.ShootingSessionEntity;
import com.frameforward.evaluation.service.ShootingSessionService;

@RestController
@RequestMapping("/shooting-sessions")
public class ShootingSessionController {
    private final ShootingSessionService sessions;
    public ShootingSessionController(ShootingSessionService sessions) {
        this.sessions = sessions;
    }
    @PostMapping
    ResponseEntity<ShootingSessionEntity> create(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestBody ShootingSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessions.create(AuthService.bearer(authorization), request));
    }
}
