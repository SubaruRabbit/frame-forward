package com.frameforward.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frameforward.auth.model.dto.AccountDeletionJob;
import com.frameforward.auth.model.dto.AccountDeletionRequest;
import com.frameforward.auth.model.dto.ChangePasswordRequest;
import com.frameforward.auth.model.dto.LoginRequest;
import com.frameforward.auth.model.dto.RefreshRequest;
import com.frameforward.auth.model.dto.RegisterRequest;
import com.frameforward.auth.model.dto.SessionTokens;
import com.frameforward.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    ResponseEntity<SessionTokens> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(auth.register(request.username(), request.email(), request.password()));
    }
    @PostMapping("/login")
    SessionTokens login(@RequestBody LoginRequest request) {
        return auth.login(request.identifier(), request.password());
    }
    @PostMapping("/refresh")
    SessionTokens refresh(@RequestBody RefreshRequest request) {
        return auth.refresh(request.refreshToken());
    }
    @PostMapping("/logout")
    ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorization) {
        auth.logout(AuthService.bearer(authorization));
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/password")
    ResponseEntity<Void> changePassword(@RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestBody ChangePasswordRequest request) {
        auth.changePassword(AuthService.bearer(authorization), request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/account-deletions")
    ResponseEntity<AccountDeletionJob> startDeletion(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestBody AccountDeletionRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(auth.startAccountDeletion(AuthService.bearer(authorization), request.currentPassword()));
    }
    @GetMapping("/account-deletions/{jobId}")
    AccountDeletionJob deletionStatus(@PathVariable String jobId,
            @RequestHeader(name = "X-Account-Deletion-Token", required = false) String token) {
        return auth.deletionStatus(jobId, token);
    }
    @PostMapping("/account-deletions/{jobId}")
    ResponseEntity<AccountDeletionJob> retryDeletion(@PathVariable String jobId,
            @RequestHeader(name = "X-Account-Deletion-Token", required = false) String token) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(auth.retryAccountDeletion(jobId, token));
    }
}
