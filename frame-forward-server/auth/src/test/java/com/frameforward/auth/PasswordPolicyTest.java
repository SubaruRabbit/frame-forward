package com.frameforward.auth;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PasswordPolicyTest {
    @Test
    void rejectsPasswordsOutsideThePermittedLength() {
        assertInvalid(null);
        assertInvalid("Ab1!");
        assertInvalid("A".repeat(65) + "1");
    }

    @Test
    void rejectsPasswordsWithOnlyOneCharacterCategory() {
        assertInvalid("abcdefgh");
        assertInvalid("12345678");
        assertInvalid("!!!!!!!!");
    }

    @Test
    void acceptsPasswordsWithAtLeastTwoCharacterCategories() {
        assertDoesNotThrow(() -> PasswordPolicy.validate("Abcdefgh"));
        assertDoesNotThrow(() -> PasswordPolicy.validate("abcdef12"));
        assertDoesNotThrow(() -> PasswordPolicy.validate("1234567!"));
    }

    private static void assertInvalid(String password) {
        assertThrows(AuthService.ValidationException.class, () -> PasswordPolicy.validate(password));
    }
}
