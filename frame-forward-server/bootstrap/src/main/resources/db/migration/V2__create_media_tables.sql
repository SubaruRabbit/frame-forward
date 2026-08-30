CREATE TABLE media (
  id VARCHAR(36) PRIMARY KEY, owner_id VARCHAR(36) NOT NULL, content_hash CHAR(64) NOT NULL,
  width INT NOT NULL, height INT NOT NULL, original_path VARCHAR(1024) NOT NULL, ai_copy_path VARCHAR(1024) NOT NULL,
  exif_json JSON NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_media_owner_hash (owner_id, content_hash)
);
