CREATE TABLE evaluation_rules (
  version VARCHAR(80) PRIMARY KEY,
  weights_json JSON NOT NULL,
  created_at TIMESTAMP(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO evaluation_rules (version, weights_json, created_at) VALUES ('v1', JSON_OBJECT('composition', 0.35, 'light', 0.30, 'subject', 0.20, 'technical', 0.15), CURRENT_TIMESTAMP(6));
