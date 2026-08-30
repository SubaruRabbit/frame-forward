CREATE TABLE user_equipment (
    id CHAR(36) NOT NULL,
    account_id CHAR(36) NOT NULL,
    kind VARCHAR(16) NOT NULL,
    catalog_item_id VARCHAR(64) NOT NULL,
    nickname VARCHAR(64) NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    primary_camera_account_id CHAR(36) GENERATED ALWAYS AS (
        CASE WHEN kind = 'CAMERA' AND is_primary THEN account_id ELSE NULL END
    ) STORED,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_equipment_owned_catalog (account_id, kind, catalog_item_id),
    UNIQUE KEY uk_user_equipment_primary_camera (primary_camera_account_id),
    KEY ix_user_equipment_account_kind (account_id, kind)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
