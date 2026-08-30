package com.frameforward.auth;

final class PasswordPolicy {
    private PasswordPolicy() {}
    static void validate(String password) {
        if (password == null || password.length() < 8 || password.length() > 64) throw new AuthService.ValidationException("password must be 8-64 characters and use two character categories");
        int categories = 0;
        if (password.chars().anyMatch(Character::isLowerCase)) categories++;
        if (password.chars().anyMatch(Character::isUpperCase)) categories++;
        if (password.chars().anyMatch(Character::isDigit)) categories++;
        if (password.chars().anyMatch(value -> !Character.isLetterOrDigit(value))) categories++;
        if (categories < 2) throw new AuthService.ValidationException("password must be 8-64 characters and use two character categories");
    }
}
