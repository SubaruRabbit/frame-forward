package com.frameforward.media.controller;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.frameforward.media.service.MediaService;
@RestControllerAdvice
class MediaExceptionHandler {
    @ExceptionHandler(MediaService.InvalidMediaException.class)
    ResponseEntity<Map<String, String>> invalid(MediaService.InvalidMediaException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(MediaService.TooLargeException.class)
    ResponseEntity<Map<String, String>> large() {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "JPEG must be 50 MB or smaller");
    }
    @ExceptionHandler(MediaService.NotFoundException.class)
    ResponseEntity<Map<String, String>> missing() {
        return error(HttpStatus.NOT_FOUND, "Media not found");
    }
    private static ResponseEntity<Map<String, String>> error(HttpStatus s, String m) {
        return ResponseEntity.status(s).body(Map.of("code", s.name(), "message", m));
    }
}
