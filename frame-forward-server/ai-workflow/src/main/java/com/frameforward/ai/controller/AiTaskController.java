package com.frameforward.ai.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskStatus;
import com.frameforward.ai.service.AiTaskRuntime;
import com.frameforward.auth.service.AuthService;

@RestController
@RequestMapping("/ai/tasks")
public class AiTaskController {
    private final AiTaskRuntime runtime;

    public AiTaskController(AiTaskRuntime runtime) {
        this.runtime = runtime;
    }

    @PostMapping
    public ResponseEntity<AiTaskCreated> create(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @RequestBody AiTaskCreateRequest request) {
        return ResponseEntity.accepted().body(runtime.create(token(authorization), key, request));
    }

    @GetMapping("/{id}")
    public AiTaskStatus get(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String id) {
        return runtime.get(token(authorization), id);
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String id) {
        return runtime.events(token(authorization), id);
    }

    static String token(String header) {
        if (header == null || !header.startsWith("Bearer "))
            throw new AuthService.InvalidSessionException();
        return header.substring(7);
    }
}
