package com.frameforward.bootstrap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.frameforward.auth.AccountDataCleanup;

class BootstrapArchitectureTest {
    @Test
    void accountDeletionConfigurationProvidesDatabaseCleanup() {
        var cleanup = new AccountDeletionConfiguration()
                .accountDatabaseCleanup(org.mockito.Mockito.mock(org.springframework.jdbc.core.JdbcTemplate.class));

        assertTrue(cleanup instanceof AccountDatabaseCleanup);
    }

    @Test
    void accountCleanupIsExplicitlyAssembledInsteadOfComponentScanned() {
        assertFalse(AccountDatabaseCleanup.class.isAnnotationPresent(Component.class));
        assertTrue(Arrays.stream(AccountDeletionConfiguration.class.getDeclaredMethods()).map(Method::getReturnType)
                .anyMatch(AccountDataCleanup.class::isAssignableFrom));
        assertTrue(Arrays.stream(AccountDeletionConfiguration.class.getDeclaredMethods())
                .anyMatch(method -> method.isAnnotationPresent(Bean.class)));
    }
}
