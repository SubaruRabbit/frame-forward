CREATE TABLE shooting_sessions (
  id VARCHAR(36) PRIMARY KEY,
  account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  shooting_plan_id VARCHAR(36) NOT NULL,
  plan_context VARCHAR(1000) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT fk_shooting_session_account FOREIGN KEY (account_id) REFERENCES accounts(id),
  CONSTRAINT fk_shooting_session_plan FOREIGN KEY (shooting_plan_id) REFERENCES shooting_plans(id),
  KEY ix_shooting_session_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE photo_evaluations
  ADD COLUMN session_id VARCHAR(36) NULL,
  ADD CONSTRAINT fk_photo_evaluation_session FOREIGN KEY (session_id) REFERENCES shooting_sessions(id),
  DROP INDEX uk_photo_evaluation_reuse,
  ADD UNIQUE KEY uk_photo_evaluation_reuse (account_id, content_hash, rule_version, execution_version, session_id);

CREATE TABLE retake_links (
  retake_evaluation_id VARCHAR(36) PRIMARY KEY,
  original_evaluation_id VARCHAR(36) NOT NULL,
  account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  session_id VARCHAR(36) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT fk_retake_link_original FOREIGN KEY (original_evaluation_id) REFERENCES photo_evaluations(id),
  CONSTRAINT fk_retake_link_retake FOREIGN KEY (retake_evaluation_id) REFERENCES photo_evaluations(id),
  CONSTRAINT fk_retake_link_account FOREIGN KEY (account_id) REFERENCES accounts(id),
  CONSTRAINT fk_retake_link_session FOREIGN KEY (session_id) REFERENCES shooting_sessions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
