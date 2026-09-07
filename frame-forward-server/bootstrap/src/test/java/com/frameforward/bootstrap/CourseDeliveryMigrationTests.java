package com.frameforward.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.DriverManager;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class CourseDeliveryMigrationTests {
    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4.0");

    @Test
    void migrationsAlignCourseAccountColumnsAndCascadeAccountDeletion() throws Exception {
        var url = MYSQL.getJdbcUrl();
        Flyway.configure().dataSource(url, MYSQL.getUsername(), MYSQL.getPassword()).locations("classpath:db/migration")
                .load().migrate();
        try (var connection = DriverManager.getConnection(url, MYSQL.getUsername(), MYSQL.getPassword())) {
            try (var statement = connection.createStatement();
                    var columns = statement.executeQuery(
                            "SELECT TABLE_NAME, COLUMN_TYPE, COLLATION_NAME FROM information_schema.COLUMNS "
                                    + "WHERE TABLE_SCHEMA = DATABASE() AND COLUMN_NAME = 'account_id' "
                                    + "AND TABLE_NAME IN ('lesson_progress', 'lesson_assignment_feedback') "
                                    + "ORDER BY TABLE_NAME")) {
                assertThat(columns.next()).isTrue();
                assertThat(columns.getString("COLUMN_TYPE")).isEqualTo("char(36)");
                assertThat(columns.getString("COLLATION_NAME")).isEqualTo("utf8mb4_0900_ai_ci");
                assertThat(columns.next()).isTrue();
                assertThat(columns.getString("COLUMN_TYPE")).isEqualTo("char(36)");
                assertThat(columns.getString("COLLATION_NAME")).isEqualTo("utf8mb4_0900_ai_ci");
                assertThat(columns.next()).isFalse();
            }
            try (var statement = connection.createStatement();
                    var columns = statement.executeQuery(
                            "SELECT TABLE_NAME, COLUMN_TYPE, COLLATION_NAME FROM information_schema.COLUMNS "
                                    + "WHERE TABLE_SCHEMA = DATABASE() AND COLUMN_NAME = 'content_version_id' "
                                    + "AND TABLE_NAME IN ('lesson_progress', 'lesson_assignment_feedback') "
                                    + "ORDER BY TABLE_NAME")) {
                assertThat(columns.next()).isTrue();
                assertThat(columns.getString("COLUMN_TYPE")).isEqualTo("varchar(36)");
                assertThat(columns.getString("COLLATION_NAME")).isEqualTo("utf8mb4_0900_ai_ci");
                assertThat(columns.next()).isTrue();
                assertThat(columns.getString("COLUMN_TYPE")).isEqualTo("varchar(36)");
                assertThat(columns.getString("COLLATION_NAME")).isEqualTo("utf8mb4_0900_ai_ci");
                assertThat(columns.next()).isFalse();
            }
            try (var statement = connection.createStatement();
                    var constraints = statement.executeQuery(
                            "SELECT TABLE_NAME, CONSTRAINT_NAME, DELETE_RULE FROM information_schema.REFERENTIAL_CONSTRAINTS "
                                    + "WHERE CONSTRAINT_SCHEMA = DATABASE() AND CONSTRAINT_NAME IN "
                                    + "('fk_lesson_progress_account', 'fk_lesson_assignment_feedback_account') "
                                    + "ORDER BY TABLE_NAME")) {
                assertThat(constraints.next()).isTrue();
                assertThat(constraints.getString("DELETE_RULE")).isEqualTo("CASCADE");
                assertThat(constraints.next()).isTrue();
                assertThat(constraints.getString("DELETE_RULE")).isEqualTo("CASCADE");
                assertThat(constraints.next()).isFalse();
            }
            try (var statement = connection.createStatement();
                    var tables = statement
                            .executeQuery("SELECT TABLE_NAME, ENGINE, TABLE_COLLATION FROM information_schema.TABLES "
                                    + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME IN "
                                    + "('course_content_versions', 'lesson_progress', 'lesson_assignment_feedback') "
                                    + "ORDER BY TABLE_NAME")) {
                while (tables.next()) {
                    assertThat(tables.getString("ENGINE")).isEqualTo("InnoDB");
                    assertThat(tables.getString("TABLE_COLLATION")).isEqualTo("utf8mb4_0900_ai_ci");
                }
            }
            var accountId = "00000000-0000-0000-0000-000000000017";
            try (var insert = connection
                    .prepareStatement("INSERT INTO accounts(id, username, email, password_hash) VALUES (?, ?, ?, ?)");
                    var content = connection.prepareStatement("INSERT INTO course_content_versions "
                            + "(id, course_id, content_version, model_id, prompt_version, source_material_version, created_at) "
                            + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP(6))");
                    var progress = connection.prepareStatement(
                            "INSERT INTO lesson_progress(account_id, content_version_id, lesson_id, completed_at) "
                                    + "VALUES (?, ?, ?, CURRENT_TIMESTAMP(6))");
                    var feedback = connection.prepareStatement("INSERT INTO lesson_assignment_feedback "
                            + "(id, account_id, content_version_id, lesson_id, media_id, feedback_task_id, lesson_objective, created_at) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP(6))");
                    var delete = connection.prepareStatement("DELETE FROM accounts WHERE id = ?");
                    var count = connection
                            .prepareStatement("SELECT (SELECT COUNT(*) FROM lesson_progress WHERE account_id = ?) "
                                    + "+ (SELECT COUNT(*) FROM lesson_assignment_feedback WHERE account_id = ?)")) {
                insert.setString(1, accountId);
                insert.setString(2, "migration-user");
                insert.setString(3, "migration-user@example.com");
                insert.setString(4, "hash");
                insert.executeUpdate();
                content.setString(1, "00000000-0000-0000-0000-000000000018");
                content.setString(2, "migration-course");
                content.setString(3, "v1");
                content.setString(4, "model");
                content.setString(5, "prompt");
                content.setString(6, "source");
                content.executeUpdate();
                progress.setString(1, accountId);
                progress.setString(2, "00000000-0000-0000-0000-000000000018");
                progress.setString(3, "lesson");
                progress.executeUpdate();
                feedback.setString(1, "00000000-0000-0000-0000-000000000019");
                feedback.setString(2, accountId);
                feedback.setString(3, "00000000-0000-0000-0000-000000000018");
                feedback.setString(4, "lesson");
                feedback.setString(5, "media");
                feedback.setString(6, "task");
                feedback.setString(7, "objective");
                feedback.executeUpdate();
                delete.setString(1, accountId);
                assertThat(delete.executeUpdate()).isEqualTo(1);
                count.setString(1, accountId);
                count.setString(2, accountId);
                try (var result = count.executeQuery()) {
                    result.next();
                    assertThat(result.getInt(1)).isZero();
                }
            }
        }
    }
}
