package com.frameforward.bootstrap.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.frameforward.auth.gateway.AccountDataCleanup;
import com.frameforward.bootstrap.FrameForwardApplication;
import com.frameforward.bootstrap.repository.AccountDatabaseCleanup;

class BootstrapArchitectureTest {
    @Test
    void mapperScanOnlyRegistersExplicitPersistenceInterfaces() {
        var scan = FrameForwardApplication.class.getAnnotation(org.mybatis.spring.annotation.MapperScan.class);
        org.junit.jupiter.api.Assertions.assertEquals(org.apache.ibatis.annotations.Mapper.class,
                scan.annotationClass());
    }

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
