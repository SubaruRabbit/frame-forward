package com.frameforward.auth;

final class PasswordPolicy {
    private static final String INVALID_PASSWORD_MESSAGE = "password must be 8-64 characters and use two character categories";

    private PasswordPolicy() {
    }

    static void validate(String password) {
        if (hasInvalidLength(password))
            reject();
        if (characterCategoryCount(password) < 2)
            reject();
    }

    private static boolean hasInvalidLength(String password) {
        return password == null || password.length() < 8 || password.length() > 64;
    }

    private static int characterCategoryCount(String password) {
        int categories = 0;
        if (password.chars().anyMatch(Character::isLowerCase))
            categories++;
        if (password.chars().anyMatch(Character::isUpperCase))
            categories++;
        if (password.chars().anyMatch(Character::isDigit))
            categories++;
        if (password.chars().anyMatch(value -> !Character.isLetterOrDigit(value)))
            categories++;
        return categories;
    }

    private static void reject() {
        throw new AuthService.ValidationException(INVALID_PASSWORD_MESSAGE);
    }
}
