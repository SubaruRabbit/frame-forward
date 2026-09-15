package com.frameforward.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class DatabaseDocumentationMigrationTests {

	private static final Pattern CHINESE = Pattern.compile("[\\u4e00-\\u9fff]");

	@Container
	static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4.0");

	@BeforeAll
	static void migrate() {
		Flyway.configure().dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
				.locations("classpath:db/migration").load().migrate();
	}

	@Test
	void documentsAuthenticationTablesAndColumnsInChinese() throws Exception {
		assertDocumentation(Map.of("accounts", List.of("id", "username", "email", "password_hash", "created_at"),
				"refresh_sessions", List.of("token_hash", "account_id", "expires_at"), "account_deletion_jobs",
				List.of("id", "account_id", "deletion_token_hash", "state", "failure_reason",
						"deletion_token_expires_at", "created_at", "updated_at")));
	}

	@Test
	void documentsMediaTablesAndColumnsInChinese() throws Exception {
		assertDocumentation(Map.of("media", List.of("id", "owner_id", "content_hash", "width", "height",
				"original_path", "ai_copy_path", "exif_json", "created_at")));
	}

	@Test
	void documentsEquipmentTablesAndColumnsInChinese() throws Exception {
		assertDocumentation(
				Map.of("catalog_cameras", List.of("id", "brand", "model", "mount", "sensor_format"), "catalog_lenses",
						List.of("id", "brand", "model", "mount", "focal_length_min_mm", "focal_length_max_mm",
								"maximum_aperture", "sensor_format"),
						"catalog_accessory_types", List.of("id", "display_name"), "user_equipment",
						List.of("id", "account_id", "kind", "catalog_item_id", "nickname", "is_primary",
								"primary_camera_account_id")));
	}

	@Test
	void documentsAiWorkflowTablesAndColumnsInChinese() throws Exception {
		assertDocumentation(Map.of("ai_tasks",
				List.of("id", "account_id", "operation_type", "idempotency_key", "state", "workflow_version",
						"model_id", "prompt_version", "rule_version", "schema_version", "input_json", "result_json",
						"error_code", "created_at", "updated_at")));
	}

	@Test
	void documentsCourseTablesAndColumnsInChinese() throws Exception {
		assertDocumentation(Map.of("course_content_versions",
				List.of("id", "course_id", "content_version", "model_id", "prompt_version", "source_material_version",
						"created_at"),
				"lesson_progress", List.of("account_id", "content_version_id", "lesson_id", "completed_at"),
				"lesson_assignment_feedback", List.of("id", "account_id", "content_version_id", "lesson_id", "media_id",
						"feedback_task_id", "lesson_objective", "created_at")));
	}

	@Test
	void documentsShootingTablesAndColumnsInChinese() throws Exception {
		assertDocumentation(Map.of("scene_analyses",
				List.of("id", "account_id", "environment_media_id", "subject_type", "subject_text", "target_style",
						"time_constraint_minutes", "equipment_snapshot_json", "ai_task_id", "created_at"),
				"shooting_plans", List.of("id", "account_id", "scene_analysis_id", "ai_task_id", "scene_snapshot_json",
						"equipment_snapshot_json", "created_at")));
	}

	@Test
	void documentsEvaluationTablesAndColumnsInChinese() throws Exception {
		assertDocumentation(Map.of("evaluation_rules", List.of("version", "weights_json", "created_at"),
				"photo_evaluations",
				List.of("id", "account_id", "media_id", "content_hash", "rule_version", "execution_version",
						"ai_task_id", "result_json", "created_at", "session_id"),
				"shooting_sessions", List.of("id", "account_id", "shooting_plan_id", "plan_context", "created_at"),
				"retake_links",
				List.of("retake_evaluation_id", "original_evaluation_id", "account_id", "session_id", "created_at")));
	}

	@Test
	void documentsGenerationTablesAndColumnsInChinese() throws Exception {
		assertDocumentation(
				Map.of("reference_images", List.of("id", "account_id", "environment_media_id", "shooting_plan_id",
						"ai_task_id", "selected_plan_label", "prompt_text", "generated_media_id", "created_at")));
	}

	@Test
	void documentsPortfolioTablesAndColumnsInChinese() throws Exception {
		assertDocumentation(Map.of("portfolio_favorites", List.of("media_id", "account_id", "created_at"),
				"portfolio_work_deletion_jobs",
				List.of("id", "account_id", "media_id", "state", "failure_reason", "created_at", "updated_at")));
	}

	private void assertDocumentation(Map<String, List<String>> expected) throws Exception {

		try (var connection = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(),
				MYSQL.getPassword())) {

			for (var entry : expected.entrySet()) {

				try (var table = connection.prepareStatement("SELECT TABLE_COMMENT FROM information_schema.TABLES "
						+ "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?")) {
					table.setString(1, entry.getKey());

					try (var result = table.executeQuery()) {
						assertThat(result.next()).as("表 %s 应存在", entry.getKey()).isTrue();
						assertThat(result.getString("TABLE_COMMENT")).as("表 %s 的中文描述", entry.getKey())
								.containsPattern(CHINESE);
					}
				}

				Map<String, String> actual = new LinkedHashMap<>();

				try (var columns = connection.prepareStatement("SELECT COLUMN_NAME, COLUMN_COMMENT "
						+ "FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? "
						+ "ORDER BY ORDINAL_POSITION")) {
					columns.setString(1, entry.getKey());

					try (var result = columns.executeQuery()) {

						while (result.next()) {
							actual.put(result.getString("COLUMN_NAME"), result.getString("COLUMN_COMMENT"));
						}
					}
				}
				assertThat(actual.keySet()).as("表 %s 的完整字段清单", entry.getKey())
						.containsExactlyElementsOf(entry.getValue());
				actual.forEach((column, comment) -> assertThat(comment).as("字段 %s.%s 的中文描述", entry.getKey(), column)
						.containsPattern(CHINESE));
			}
		}
	}

}
