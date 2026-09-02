package com.frameforward.ai;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
class AiTaskExceptionHandler {
    @ExceptionHandler(AiTaskRuntime.BadRequest.class)
    ResponseEntity<Map<String, String>> bad() {
        return ResponseEntity.badRequest()
                .body(Map.of("code", "INVALID_REQUEST", "message", "Invalid AI task request."));
    }
    @ExceptionHandler(AiTaskRuntime.NotFound.class)
    ResponseEntity<Void> missing() {
        return ResponseEntity.notFound().build();
    }
}
