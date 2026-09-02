package com.frameforward.shooting;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthController;

@RestController
@RequestMapping("/scene-analyses")
public class SceneAnalysisController {
    private final SceneAnalysisService analyses;
    public SceneAnalysisController(SceneAnalysisService analyses) {
        this.analyses = analyses;
    }
    @PostMapping
    ResponseEntity<AiTaskRuntime.Created> create(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @RequestBody SceneAnalysisService.Request request) {
        return ResponseEntity.accepted().body(analyses.create(AuthController.bearer(authorization), key, request));
    }
}
