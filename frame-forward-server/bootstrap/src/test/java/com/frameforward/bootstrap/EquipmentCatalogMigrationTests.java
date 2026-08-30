package com.frameforward.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.DriverManager;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

class EquipmentCatalogMigrationTests {
    @Test void migrationsApplyToAnEmptyMysqlDatabase() throws Exception {
        var database = "ff_catalog_test_" + UUID.randomUUID().toString().replace("-", "");
        var serverUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
        try (var connection = DriverManager.getConnection(serverUrl, "root", "123456"); var statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE `" + database + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci");
            var url = "jdbc:mysql://localhost:3306/" + database + "?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
            Flyway.configure().dataSource(url, "root", "123456").locations("classpath:db/migration").target("4").load().migrate();
            try (var catalog = DriverManager.getConnection(url, "root", "123456"); var catalogStatement = catalog.createStatement()) {
                try (var result = catalogStatement.executeQuery("SELECT COUNT(*) FROM catalog_cameras")) {
                    result.next(); assertThat(result.getInt(1)).isEqualTo(8);
                }
                try (var result = catalogStatement.executeQuery("SELECT COUNT(DISTINCT brand) FROM catalog_lenses")) {
                    result.next(); assertThat(result.getInt(1)).isEqualTo(6);
                }
                try (var result = catalogStatement.executeQuery("SELECT COUNT(*) FROM catalog_accessory_types")) {
                    result.next(); assertThat(result.getInt(1)).isEqualTo(7);
                }
            }
        } finally {
            try (var connection = DriverManager.getConnection(serverUrl, "root", "123456"); var statement = connection.createStatement()) {
                statement.execute("DROP DATABASE IF EXISTS `" + database + "`");
            }
        }
    }
}
