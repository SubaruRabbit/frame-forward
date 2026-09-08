package com.frameforward.equipment.controller;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.frameforward.equipment.business.CatalogNotFoundException;

@RestControllerAdvice
class CatalogExceptionHandler {
    @ExceptionHandler(CatalogNotFoundException.class)
    ResponseEntity<Map<String, String>> catalogItemNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("code", "CATALOG_ITEM_NOT_FOUND", "message", "The catalog item does not exist."));
    }
}
