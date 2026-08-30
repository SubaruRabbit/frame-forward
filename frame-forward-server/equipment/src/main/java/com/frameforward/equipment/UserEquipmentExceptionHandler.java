package com.frameforward.equipment;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class UserEquipmentExceptionHandler {
    @ExceptionHandler(UserEquipmentService.EquipmentNotFoundException.class)
    ResponseEntity<Map<String, String>> notFound() { return error(HttpStatus.NOT_FOUND, "EQUIPMENT_NOT_FOUND", "The owned equipment does not exist."); }
    @ExceptionHandler(UserEquipmentService.DuplicateEquipmentException.class)
    ResponseEntity<Map<String, String>> duplicate() { return error(HttpStatus.CONFLICT, "EQUIPMENT_ALREADY_OWNED", "The catalog item is already owned."); }
    @ExceptionHandler({UserEquipmentService.NotCameraException.class, UserEquipmentService.InvalidEquipmentException.class})
    ResponseEntity<Map<String, String>> invalid() { return error(HttpStatus.BAD_REQUEST, "INVALID_EQUIPMENT", "The equipment request is invalid."); }
    private ResponseEntity<Map<String, String>> error(HttpStatus status, String code, String message) { return ResponseEntity.status(status).body(Map.of("code", code, "message", message)); }
}
