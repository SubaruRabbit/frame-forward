package com.frameforward.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.frameforward.common.architecture.JavaLayerPackages;

/** 生产类型必须按职责分包，不允许恢复迁移期根包入口。 */
class AuthPackageInventoryTest {
    @Test
    void allProductionTypesUseTheirLayerDirectories() throws Exception {
        var violations = JavaLayerPackages.inspect(Path.of("src/main/java"), "com.frameforward.auth");
        assertTrue(violations.isEmpty(), violations.toString());
    }

    @Test
    void controllerRequestsAreSeparateDataTypes() throws Exception {
        Class<?> controller = Class.forName("com.frameforward.auth.controller.AuthController");
        assertTrue(java.util.Arrays.stream(controller.getDeclaredClasses()).noneMatch(Class::isRecord));
        for (String name : new String[]{"RegisterRequest", "LoginRequest", "RefreshRequest", "ChangePasswordRequest",
                "AccountDeletionRequest"})
            assertTrue(Class.forName("com.frameforward.auth.model.dto." + name).isRecord());
    }
}
