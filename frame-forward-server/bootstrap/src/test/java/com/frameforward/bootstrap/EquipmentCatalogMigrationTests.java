package com.frameforward.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.DriverManager;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class EquipmentCatalogMigrationTests {
    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4.0");

    @Test
    void migrationsApplyToAnEmptyMysqlDatabase() throws Exception {
        var url = MYSQL.getJdbcUrl();
        Flyway.configure().dataSource(url, MYSQL.getUsername(), MYSQL.getPassword()).locations("classpath:db/migration")
                .target("4").load().migrate();
        try (var connection = DriverManager.getConnection(url, MYSQL.getUsername(), MYSQL.getPassword());
                var catalogStatement = connection.createStatement()) {
            try (var result = catalogStatement.executeQuery("SELECT COUNT(*) FROM catalog_cameras")) {
                result.next();
                assertThat(result.getInt(1)).isEqualTo(8);
            }
            try (var result = catalogStatement.executeQuery("SELECT COUNT(DISTINCT brand) FROM catalog_lenses")) {
                result.next();
                assertThat(result.getInt(1)).isEqualTo(6);
            }
            try (var result = catalogStatement.executeQuery("SELECT COUNT(*) FROM catalog_accessory_types")) {
                result.next();
                assertThat(result.getInt(1)).isEqualTo(7);
            }
        }
    }
}
