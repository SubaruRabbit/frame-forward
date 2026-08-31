CREATE TABLE photo_evaluations (
  id VARCHAR(36) PRIMARY KEY,
  account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  media_id VARCHAR(36) NOT NULL,
  content_hash CHAR(64) NOT NULL,
  rule_version VARCHAR(80) NOT NULL,
  execution_version VARCHAR(120) NOT NULL,
  ai_task_id VARCHAR(36) NOT NULL,
  result_json JSON NULL,
  created_at TIMESTAMP(6) NOT NULL,
  UNIQUE KEY uk_photo_evaluation_reuse (account_id, content_hash, rule_version, execution_version),
  CONSTRAINT fk_photo_evaluation_account FOREIGN KEY (account_id) REFERENCES accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
