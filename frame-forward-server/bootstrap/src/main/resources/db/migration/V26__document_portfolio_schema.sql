ALTER TABLE portfolio_favorites COMMENT = '作品集收藏表',
  MODIFY COLUMN media_id VARCHAR(36) NOT NULL COMMENT '收藏的媒体唯一标识',
  MODIFY COLUMN account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属账户唯一标识',
  MODIFY COLUMN created_at TIMESTAMP(6) NOT NULL COMMENT '收藏创建时间';

ALTER TABLE portfolio_work_deletion_jobs COMMENT = '作品删除任务表',
  MODIFY COLUMN id VARCHAR(36) NOT NULL COMMENT '删除任务唯一标识',
  MODIFY COLUMN account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属账户唯一标识',
  MODIFY COLUMN media_id VARCHAR(36) NOT NULL COMMENT '待删除媒体唯一标识',
  MODIFY COLUMN state VARCHAR(20) NOT NULL COMMENT '删除任务状态',
  MODIFY COLUMN failure_reason VARCHAR(500) NULL COMMENT '删除失败原因',
  MODIFY COLUMN created_at TIMESTAMP(6) NOT NULL COMMENT '删除任务创建时间',
  MODIFY COLUMN updated_at TIMESTAMP(6) NOT NULL COMMENT '删除任务更新时间';
