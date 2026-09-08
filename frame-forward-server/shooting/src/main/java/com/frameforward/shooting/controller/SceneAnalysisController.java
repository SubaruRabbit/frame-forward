package com.frameforward.shooting.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.auth.service.AuthService;
import com.frameforward.shooting.model.dto.SceneAnalysisRequest;
import com.frameforward.shooting.service.SceneAnalysisService;

@RestController
@RequestMapping("/scene-analyses")
public class SceneAnalysisController {
    private final SceneAnalysisService analyses;
    public SceneAnalysisController(SceneAnalysisService analyses) {
        this.analyses = analyses;
    }
    @PostMapping
    ResponseEntity<AiTaskCreated> create(@RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @RequestBody SceneAnalysisRequest request) {
        return ResponseEntity.accepted().body(analyses.create(AuthService.bearer(authorization), key, request));
    }
}
