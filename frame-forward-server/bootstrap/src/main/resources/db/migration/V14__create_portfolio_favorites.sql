CREATE TABLE portfolio_favorites (
  media_id VARCHAR(36) NOT NULL,
  account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  PRIMARY KEY (media_id),
  CONSTRAINT fk_portfolio_favorite_account FOREIGN KEY (account_id) REFERENCES accounts(id),
  KEY ix_portfolio_favorite_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
