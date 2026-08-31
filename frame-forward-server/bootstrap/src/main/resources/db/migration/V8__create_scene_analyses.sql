CREATE TABLE scene_analyses (
  id VARCHAR(36) PRIMARY KEY,
  account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  environment_media_id VARCHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  subject_type VARCHAR(64) NOT NULL,
  subject_text VARCHAR(255) NOT NULL,
  target_style VARCHAR(255) NOT NULL,
  time_constraint_minutes INT NOT NULL,
  equipment_snapshot_json JSON NOT NULL,
  ai_task_id VARCHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT fk_scene_analysis_account FOREIGN KEY (account_id) REFERENCES accounts(id),
  KEY ix_scene_analysis_media (environment_media_id),
  KEY ix_scene_analysis_task (ai_task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
