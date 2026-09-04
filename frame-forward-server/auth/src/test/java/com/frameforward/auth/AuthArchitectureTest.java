package com.frameforward.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

class AuthArchitectureTest {
    @Test
    void serviceAndBusinessDoNotDependOnMappers() {
        assertNoMapperDependency(AuthService.class);
        assertNoMapperDependency(AuthBusiness.class);
        assertTrue(Arrays.stream(AuthService.class.getDeclaredFields())
                .anyMatch(field -> field.getType() == AuthBusiness.class));
        assertTrue(Arrays.stream(AuthBusiness.class.getDeclaredFields())
                .anyMatch(field -> field.getType() == AuthManager.class));
    }
    private static void assertNoMapperDependency(Class<?> type) {
        assertFalse(Arrays.stream(type.getDeclaredFields()).map(field -> field.getType())
                .anyMatch(BaseMapper.class::isAssignableFrom));
    }
}
