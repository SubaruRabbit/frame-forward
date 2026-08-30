CREATE TABLE catalog_cameras (
    id VARCHAR(64) NOT NULL, brand VARCHAR(32) NOT NULL, model VARCHAR(64) NOT NULL,
    mount VARCHAR(16) NOT NULL, sensor_format VARCHAR(16) NOT NULL,
    PRIMARY KEY (id), UNIQUE KEY uk_catalog_cameras_brand_model (brand, model)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE catalog_lenses (
    id VARCHAR(64) NOT NULL, brand VARCHAR(32) NOT NULL, model VARCHAR(128) NOT NULL,
    mount VARCHAR(16) NOT NULL, focal_length_min_mm SMALLINT NOT NULL, focal_length_max_mm SMALLINT NOT NULL,
    maximum_aperture DECIMAL(3,1) NOT NULL, sensor_format VARCHAR(16) NOT NULL,
    PRIMARY KEY (id), UNIQUE KEY uk_catalog_lenses_brand_model (brand, model)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE catalog_accessory_types (
    id VARCHAR(64) NOT NULL, display_name VARCHAR(64) NOT NULL,
    PRIMARY KEY (id), UNIQUE KEY uk_catalog_accessory_types_display_name (display_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
