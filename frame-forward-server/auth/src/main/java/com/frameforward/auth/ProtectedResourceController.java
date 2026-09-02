package com.frameforward.auth;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedResourceController {
    private final AuthService auth;
    public ProtectedResourceController(AuthService auth) {
        this.auth = auth;
    }
    @GetMapping("/test/protected")
    Map<String, String> protectedResource(
            @RequestHeader(name = "Authorization", required = false) String authorization) {
        return Map.of("accountId", auth.requireAccountId(AuthController.bearer(authorization)));
    }
}
