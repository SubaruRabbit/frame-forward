package com.frameforward.auth.model.dto;

public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
