package com.frameforward.evaluation.controller;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.frameforward.evaluation.business.PhotoEvaluationInvalid;
import com.frameforward.evaluation.business.PhotoEvaluationNotFound;
import com.frameforward.evaluation.business.RetakeComparisonInvalid;
import com.frameforward.evaluation.business.RetakeComparisonNotFound;
import com.frameforward.evaluation.business.ShootingSessionInvalid;
import com.frameforward.evaluation.business.ShootingSessionNotFound;
@RestControllerAdvice
class PhotoEvaluationExceptionHandler {
    @ExceptionHandler({PhotoEvaluationInvalid.class, ShootingSessionInvalid.class, RetakeComparisonInvalid.class})
    ResponseEntity<Map<String, String>> invalid() {
        return ResponseEntity.badRequest().body(Map.of("code", "INVALID_EVALUATION", "message", "照片评分或重拍对比请求无效。"));
    }
    @ExceptionHandler({PhotoEvaluationNotFound.class, ShootingSessionNotFound.class, RetakeComparisonNotFound.class})
    ResponseEntity<Map<String, String>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("code", "EVALUATION_MEDIA_NOT_FOUND", "message", "照片、方案或会话不存在或不属于当前用户。"));
    }
}
