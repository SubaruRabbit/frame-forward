package com.frameforward.equipment;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class CatalogExceptionHandler {
    @ExceptionHandler(CatalogService.CatalogNotFoundException.class)
    ResponseEntity<Map<String, String>> catalogItemNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("code", "CATALOG_ITEM_NOT_FOUND", "message", "The catalog item does not exist."));
    }
}
