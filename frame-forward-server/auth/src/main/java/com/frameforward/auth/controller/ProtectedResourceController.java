package com.frameforward.auth.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.frameforward.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProtectedResourceController {

	private final AuthService auth;

	@GetMapping("/test/protected")
	Map<String, String> protectedResource(
			@RequestHeader(name = "Authorization", required = false) String authorization) {
		return Map.of("accountId", auth.requireAccountId(AuthService.bearer(authorization)));
	}

}
