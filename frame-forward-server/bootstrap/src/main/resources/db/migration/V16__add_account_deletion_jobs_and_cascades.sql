CREATE TABLE IF NOT EXISTS account_deletion_jobs (
  id CHAR(36) NOT NULL,
  account_id CHAR(36) NOT NULL,
  deletion_token_hash CHAR(44) NOT NULL,
  state VARCHAR(20) NOT NULL,
  failure_reason VARCHAR(500) NULL,
  deletion_token_expires_at TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  updated_at TIMESTAMP(6) NOT NULL,
  PRIMARY KEY (id),
  KEY ix_account_deletion_job_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- USER_OWNED_CLEANUP: deletion jobs retain minimal status only until their short-lived token expires.
ALTER TABLE ai_tasks DROP FOREIGN KEY fk_ai_task_account;
ALTER TABLE ai_tasks ADD CONSTRAINT fk_ai_task_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
ALTER TABLE scene_analyses DROP FOREIGN KEY fk_scene_analysis_account;
ALTER TABLE scene_analyses ADD CONSTRAINT fk_scene_analysis_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
ALTER TABLE shooting_plans DROP FOREIGN KEY fk_shooting_plan_account;
ALTER TABLE shooting_plans ADD CONSTRAINT fk_shooting_plan_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
ALTER TABLE photo_evaluations DROP FOREIGN KEY fk_photo_evaluation_account;
ALTER TABLE photo_evaluations ADD CONSTRAINT fk_photo_evaluation_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
ALTER TABLE reference_images DROP FOREIGN KEY fk_reference_image_account;
ALTER TABLE reference_images ADD CONSTRAINT fk_reference_image_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
ALTER TABLE shooting_sessions DROP FOREIGN KEY fk_shooting_session_account;
ALTER TABLE shooting_sessions ADD CONSTRAINT fk_shooting_session_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
ALTER TABLE retake_links DROP FOREIGN KEY fk_retake_link_account;
ALTER TABLE retake_links ADD CONSTRAINT fk_retake_link_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
ALTER TABLE portfolio_favorites DROP FOREIGN KEY fk_portfolio_favorite_account;
ALTER TABLE portfolio_favorites ADD CONSTRAINT fk_portfolio_favorite_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
ALTER TABLE portfolio_work_deletion_jobs DROP FOREIGN KEY fk_portfolio_work_deletion_account;
ALTER TABLE portfolio_work_deletion_jobs ADD CONSTRAINT fk_portfolio_work_deletion_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
SET @media_fk := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'media' AND CONSTRAINT_NAME = 'fk_media_account' LIMIT 1);
SET @media_fk_sql := IF(@media_fk IS NULL, 'SELECT 1', 'ALTER TABLE media DROP FOREIGN KEY fk_media_account');
PREPARE drop_media_fk FROM @media_fk_sql;
EXECUTE drop_media_fk;
DEALLOCATE PREPARE drop_media_fk;
ALTER TABLE media MODIFY owner_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL;
ALTER TABLE media ADD CONSTRAINT fk_media_account FOREIGN KEY (owner_id) REFERENCES accounts(id) ON DELETE CASCADE;
-- USER_OWNED_CLEANUP: user_equipment, lesson_progress and lesson_assignment_feedback
-- are cleared by bootstrap AccountDatabaseCleanup before the account row is removed.
