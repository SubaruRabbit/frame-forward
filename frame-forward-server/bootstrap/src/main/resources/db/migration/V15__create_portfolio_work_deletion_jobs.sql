CREATE TABLE portfolio_work_deletion_jobs (
  id VARCHAR(36) PRIMARY KEY,
  account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  media_id VARCHAR(36) NOT NULL,
  state VARCHAR(20) NOT NULL,
  failure_reason VARCHAR(500) NULL,
  created_at TIMESTAMP(6) NOT NULL,
  updated_at TIMESTAMP(6) NOT NULL,
  UNIQUE KEY uk_portfolio_work_deletion (account_id, media_id),
  CONSTRAINT fk_portfolio_work_deletion_account FOREIGN KEY (account_id) REFERENCES accounts(id),
  KEY ix_portfolio_work_deletion_media (media_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
