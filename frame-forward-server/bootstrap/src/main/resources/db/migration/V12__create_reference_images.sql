CREATE TABLE reference_images (
  id VARCHAR(36) PRIMARY KEY,
  account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  environment_media_id VARCHAR(36) NOT NULL,
  shooting_plan_id VARCHAR(36) NOT NULL,
  ai_task_id VARCHAR(36) NOT NULL,
  selected_plan_label VARCHAR(32) NOT NULL,
  prompt_text TEXT NOT NULL,
  generated_media_id VARCHAR(36) NULL,
  created_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT fk_reference_image_account FOREIGN KEY (account_id) REFERENCES accounts(id),
  KEY ix_reference_image_plan (shooting_plan_id), KEY ix_reference_image_task (ai_task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
