package com.frameforward.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    ResponseEntity<AuthService.SessionTokens> register(@RequestBody RegisterRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(auth.register(request.username(), request.email(), request.password())); }
    @PostMapping("/login")
    AuthService.SessionTokens login(@RequestBody LoginRequest request) { return auth.login(request.identifier(), request.password()); }
    @PostMapping("/refresh")
    AuthService.SessionTokens refresh(@RequestBody RefreshRequest request) { return auth.refresh(request.refreshToken()); }
    @PostMapping("/logout")
    ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorization) { auth.logout(bearer(authorization)); return ResponseEntity.noContent().build(); }
    @PutMapping("/password")
    ResponseEntity<Void> changePassword(@RequestHeader(name = "Authorization", required = false) String authorization, @RequestBody ChangePasswordRequest request) { auth.changePassword(bearer(authorization), request.currentPassword(), request.newPassword()); return ResponseEntity.noContent().build(); }
    public static String bearer(String authorization) { if (authorization == null || !authorization.startsWith("Bearer ")) throw new AuthService.InvalidSessionException(); return authorization.substring(7); }
    record RegisterRequest(String username, String email, String password) {}
    record LoginRequest(String identifier, String password) {}
    record RefreshRequest(String refreshToken) {}
    record ChangePasswordRequest(String currentPassword, String newPassword) {}
}
