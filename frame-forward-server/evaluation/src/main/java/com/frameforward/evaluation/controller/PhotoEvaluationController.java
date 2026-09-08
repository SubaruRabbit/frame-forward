package com.frameforward.evaluation.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskStatus;
import com.frameforward.auth.service.AuthService;
import com.frameforward.evaluation.model.dto.PhotoEvaluationRequest;
import com.frameforward.evaluation.service.PhotoEvaluationService;
@RestController
@RequestMapping("/photo-evaluations")
public class PhotoEvaluationController {
    private final PhotoEvaluationService evaluations;
    public PhotoEvaluationController(PhotoEvaluationService evaluations) {
        this.evaluations = evaluations;
    }
    @PostMapping
    ResponseEntity<AiTaskCreated> create(@RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @RequestBody PhotoEvaluationRequest request) {
        return ResponseEntity.accepted().body(evaluations.create(AuthService.bearer(authorization), key, request));
    }
    @GetMapping("/{taskId}")
    AiTaskStatus result(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String taskId) {
        return evaluations.result(AuthService.bearer(authorization), taskId);
    }
}
