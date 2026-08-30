CREATE TABLE accounts (
    id CHAR(36) NOT NULL,
    username VARCHAR(32) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_accounts_username (username),
    UNIQUE KEY uk_accounts_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE refresh_sessions (
    token_hash CHAR(44) NOT NULL,
    account_id CHAR(36) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (token_hash),
    CONSTRAINT fk_refresh_sessions_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
