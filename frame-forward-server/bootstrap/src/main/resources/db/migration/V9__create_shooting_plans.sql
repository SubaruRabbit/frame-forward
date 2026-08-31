CREATE TABLE shooting_plans (
  id VARCHAR(36) PRIMARY KEY,
  account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  scene_analysis_id VARCHAR(36) NOT NULL,
  ai_task_id VARCHAR(36) NOT NULL,
  scene_snapshot_json JSON NOT NULL,
  equipment_snapshot_json JSON NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT fk_shooting_plan_account FOREIGN KEY (account_id) REFERENCES accounts(id),
  KEY ix_shooting_plan_scene (scene_analysis_id), KEY ix_shooting_plan_task (ai_task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
