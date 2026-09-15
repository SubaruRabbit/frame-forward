ALTER TABLE evaluation_rules COMMENT = '照片评估规则表',
  MODIFY COLUMN version VARCHAR(80) NOT NULL COMMENT '评估规则版本',
  MODIFY COLUMN weights_json JSON NOT NULL COMMENT '评估维度权重配置',
  MODIFY COLUMN created_at TIMESTAMP(6) NOT NULL COMMENT '评估规则创建时间';

ALTER TABLE photo_evaluations COMMENT = '照片评估结果表',
  MODIFY COLUMN id VARCHAR(36) NOT NULL COMMENT '照片评估唯一标识',
  MODIFY COLUMN account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属账户唯一标识',
  MODIFY COLUMN media_id VARCHAR(36) NOT NULL COMMENT '被评估媒体唯一标识',
  MODIFY COLUMN content_hash CHAR(64) NOT NULL COMMENT '被评估媒体内容哈希值',
  MODIFY COLUMN rule_version VARCHAR(80) NOT NULL COMMENT '使用的评估规则版本',
  MODIFY COLUMN execution_version VARCHAR(120) NOT NULL COMMENT '评估执行版本',
  MODIFY COLUMN ai_task_id VARCHAR(36) NOT NULL COMMENT '评估 AI 任务唯一标识',
  MODIFY COLUMN result_json JSON NULL COMMENT '照片评估结果',
  MODIFY COLUMN created_at TIMESTAMP(6) NOT NULL COMMENT '照片评估创建时间',
  MODIFY COLUMN session_id VARCHAR(36) NULL COMMENT '所属拍摄会话唯一标识';

ALTER TABLE shooting_sessions COMMENT = '拍摄会话表',
  MODIFY COLUMN id VARCHAR(36) NOT NULL COMMENT '拍摄会话唯一标识',
  MODIFY COLUMN account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属账户唯一标识',
  MODIFY COLUMN shooting_plan_id VARCHAR(36) NOT NULL COMMENT '使用的拍摄方案唯一标识',
  MODIFY COLUMN plan_context VARCHAR(1000) NOT NULL COMMENT '拍摄方案上下文摘要',
  MODIFY COLUMN created_at TIMESTAMP(6) NOT NULL COMMENT '拍摄会话创建时间';

ALTER TABLE retake_links COMMENT = '重拍评估关联表',
  MODIFY COLUMN retake_evaluation_id VARCHAR(36) NOT NULL COMMENT '重拍照片评估唯一标识',
  MODIFY COLUMN original_evaluation_id VARCHAR(36) NOT NULL COMMENT '原始照片评估唯一标识',
  MODIFY COLUMN account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属账户唯一标识',
  MODIFY COLUMN session_id VARCHAR(36) NOT NULL COMMENT '所属拍摄会话唯一标识',
  MODIFY COLUMN created_at TIMESTAMP(6) NOT NULL COMMENT '重拍关联创建时间';
