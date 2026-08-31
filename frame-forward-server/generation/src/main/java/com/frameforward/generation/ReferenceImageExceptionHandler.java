package com.frameforward.generation;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ReferenceImageExceptionHandler {
  @ExceptionHandler(ReferenceImageService.InvalidRequest.class)
  ResponseEntity<Map<String, String>> invalid() { return error(HttpStatus.BAD_REQUEST, "INVALID_REFERENCE_IMAGE", "参考图请求无效。"); }
  @ExceptionHandler(ReferenceImageService.OwnershipMissing.class)
  ResponseEntity<Map<String, String>> ownership() { return error(HttpStatus.NOT_FOUND, "REFERENCE_CONTEXT_NOT_FOUND", "现场照片或拍摄方案不属于当前用户。"); }
  private ResponseEntity<Map<String, String>> error(HttpStatus status, String code, String message) { return ResponseEntity.status(status).body(Map.of("code", code, "message", message)); }
}
