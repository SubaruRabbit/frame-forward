package com.frameforward.auth.model.dto;

public record SessionTokens(String accessToken, String refreshToken, long expiresIn) {
}
