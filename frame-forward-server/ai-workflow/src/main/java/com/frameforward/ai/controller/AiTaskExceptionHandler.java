package com.frameforward.ai.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.frameforward.ai.business.AiTaskBadRequest;
import com.frameforward.ai.business.AiTaskNotFound;

@RestControllerAdvice
class AiTaskExceptionHandler {

    @ExceptionHandler(AiTaskBadRequest.class)
    ResponseEntity<Map<String, String>> bad() {
        return ResponseEntity.badRequest()
                .body(Map.of("code", "INVALID_REQUEST", "message", "Invalid AI task request."));
    }

    @ExceptionHandler(AiTaskNotFound.class)
    ResponseEntity<Void> missing() {
        return ResponseEntity.notFound().build();
    }

}
