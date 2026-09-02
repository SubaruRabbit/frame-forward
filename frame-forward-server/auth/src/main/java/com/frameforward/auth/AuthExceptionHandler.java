package com.frameforward.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class AuthExceptionHandler {
    @ExceptionHandler(AuthService.ConflictException.class)
    ResponseEntity<Map<String, String>> conflict() {
        return error(HttpStatus.CONFLICT, "IDENTIFIER_TAKEN", "Username or email is already registered.");
    }
    @ExceptionHandler(AuthService.InvalidCredentialsException.class)
    ResponseEntity<Map<String, String>> invalidCredentials() {
        return error(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username, email, or password.");
    }
    @ExceptionHandler(AuthService.InvalidSessionException.class)
    ResponseEntity<Map<String, String>> invalidSession() {
        return error(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "A valid session is required.");
    }
    @ExceptionHandler(AuthService.ValidationException.class)
    ResponseEntity<Map<String, String>> invalidRequest(AuthService.ValidationException exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage());
    }
    private static ResponseEntity<Map<String, String>> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(Map.of("code", code, "message", message));
    }
}
