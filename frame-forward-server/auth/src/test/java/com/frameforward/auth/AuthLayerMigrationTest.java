package com.frameforward.auth;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class AuthLayerMigrationTest {
    @Test
    void persistenceAndApplicationLayersUseDedicatedPackages() throws Exception {
        Path root = Path.of("src/main/java/com/frameforward/auth");
        for (String relative : new String[]{"model/entity/AccountEntity.java", "mapper/AccountMapper.java",
                "repository/AuthRepository.java", "manager/AuthManager.java", "business/AuthBusiness.java",
                "service/AuthService.java", "model/dto/SessionTokens.java", "model/dto/AccountDeletionJob.java"})
            assertTrue(Files.isRegularFile(root.resolve(relative)), relative);
        for (String relative : new String[]{"service/AuthService.java", "business/AuthBusiness.java",
                "manager/AuthManager.java"}) {
            String source = Files.readString(root.resolve(relative));
            assertFalse(source.contains("com.baomidou"), relative);
            assertFalse(source.contains("auth.mapper"), relative);
        }
    }
}
