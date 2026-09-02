package com.frameforward.evaluation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthController;
@RestController
@RequestMapping("/photo-evaluations")
public class PhotoEvaluationController {
    private final PhotoEvaluationService evaluations;
    public PhotoEvaluationController(PhotoEvaluationService evaluations) {
        this.evaluations = evaluations;
    }
    @PostMapping
    ResponseEntity<AiTaskRuntime.Created> create(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @RequestBody PhotoEvaluationService.Request request) {
        return ResponseEntity.accepted().body(evaluations.create(AuthController.bearer(authorization), key, request));
    }
    @GetMapping("/{taskId}")
    AiTaskRuntime.Status result(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String taskId) {
        return evaluations.result(AuthController.bearer(authorization), taskId);
    }
}
