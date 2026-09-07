ALTER TABLE lesson_progress
  DROP FOREIGN KEY fk_progress_course_version;

ALTER TABLE course_content_versions
  ENGINE = InnoDB,
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

ALTER TABLE lesson_progress
  MODIFY account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  MODIFY content_version_id VARCHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  ENGINE = InnoDB,
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  ADD CONSTRAINT fk_progress_course_version FOREIGN KEY (content_version_id) REFERENCES course_content_versions(id),
  ADD CONSTRAINT fk_lesson_progress_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;

ALTER TABLE lesson_assignment_feedback
  MODIFY account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  MODIFY content_version_id VARCHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  ENGINE = InnoDB,
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  ADD CONSTRAINT fk_lesson_assignment_feedback_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
