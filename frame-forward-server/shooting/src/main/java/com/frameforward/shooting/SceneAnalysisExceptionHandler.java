package com.frameforward.shooting;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class SceneAnalysisExceptionHandler {
    @ExceptionHandler(SceneAnalysisService.InvalidRequest.class)
    ResponseEntity<Map<String, String>> invalid() {
        return error(HttpStatus.BAD_REQUEST, "INVALID_SCENE_ANALYSIS", "场景分析请求不完整或无效。");
    }
    @ExceptionHandler({SceneAnalysisService.MediaNotOwned.class, SceneAnalysisService.EquipmentNotOwned.class,
            ShootingPlanService.SceneNotFound.class})
    ResponseEntity<Map<String, String>> notFound() {
        return error(HttpStatus.NOT_FOUND, "SCENE_CONTEXT_NOT_FOUND", "请求的私有媒体或器材不存在。");
    }
    @ExceptionHandler(ShootingPlanService.InvalidRequest.class)
    ResponseEntity<Map<String, String>> invalidPlan() {
        return error(HttpStatus.BAD_REQUEST, "INVALID_SHOOTING_PLAN", "拍摄方案请求无效。");
    }
    private ResponseEntity<Map<String, String>> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(Map.of("code", code, "message", message));
    }
}
