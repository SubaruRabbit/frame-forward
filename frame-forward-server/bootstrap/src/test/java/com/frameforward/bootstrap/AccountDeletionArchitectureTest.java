package com.frameforward.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class AccountDeletionArchitectureTest {
    @Test
    void everyUserOwnedTableDeclaresCascadeOrCleanup() throws Exception {
        String migration = Files
                .readString(Path.of("src/main/resources/db/migration/V16__add_account_deletion_jobs_and_cascades.sql"));
        for (String table : new String[]{"ai_tasks", "scene_analyses", "shooting_plans", "photo_evaluations",
                "reference_images", "shooting_sessions", "retake_links", "portfolio_favorites",
                "portfolio_work_deletion_jobs", "media"})
            assertThat(migration).contains("ALTER TABLE " + table).contains("ON DELETE CASCADE");
        assertThat(migration)
                .contains("USER_OWNED_CLEANUP: user_equipment, lesson_progress and lesson_assignment_feedback");
        String cleanup = Files
                .readString(Path.of("src/main/java/com/frameforward/bootstrap/AccountDatabaseCleanup.java"));
        for (String table : new String[]{"user_equipment", "lesson_progress", "lesson_assignment_feedback"})
            assertThat(cleanup).contains("DELETE FROM " + table);
    }
}
