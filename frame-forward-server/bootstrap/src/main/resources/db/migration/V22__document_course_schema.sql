ALTER TABLE course_content_versions COMMENT = '课程内容版本表',
  MODIFY COLUMN id VARCHAR(36) NOT NULL COMMENT '课程内容版本唯一标识',
  MODIFY COLUMN course_id VARCHAR(80) NOT NULL COMMENT '课程唯一标识',
  MODIFY COLUMN content_version VARCHAR(80) NOT NULL COMMENT '课程内容版本号',
  MODIFY COLUMN model_id VARCHAR(80) NOT NULL COMMENT '生成课程内容使用的模型标识',
  MODIFY COLUMN prompt_version VARCHAR(80) NOT NULL COMMENT '生成课程内容使用的提示词版本',
  MODIFY COLUMN source_material_version VARCHAR(80) NOT NULL COMMENT '课程源材料版本',
  MODIFY COLUMN created_at TIMESTAMP(6) NOT NULL COMMENT '课程内容版本创建时间';

ALTER TABLE lesson_progress COMMENT = '课程课时学习进度表',
  MODIFY COLUMN account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属账户唯一标识',
  MODIFY COLUMN content_version_id VARCHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程内容版本唯一标识',
  MODIFY COLUMN lesson_id VARCHAR(80) NOT NULL COMMENT '课时唯一标识',
  MODIFY COLUMN completed_at TIMESTAMP(6) NOT NULL COMMENT '课时完成时间';

ALTER TABLE lesson_assignment_feedback COMMENT = '课时作业反馈表',
  MODIFY COLUMN id VARCHAR(36) NOT NULL COMMENT '作业反馈唯一标识',
  MODIFY COLUMN account_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属账户唯一标识',
  MODIFY COLUMN content_version_id VARCHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程内容版本唯一标识',
  MODIFY COLUMN lesson_id VARCHAR(80) NOT NULL COMMENT '课时唯一标识',
  MODIFY COLUMN media_id VARCHAR(36) NOT NULL COMMENT '作业媒体唯一标识',
  MODIFY COLUMN feedback_task_id VARCHAR(36) NOT NULL COMMENT '反馈 AI 任务唯一标识',
  MODIFY COLUMN lesson_objective VARCHAR(500) NOT NULL COMMENT '课时训练目标',
  MODIFY COLUMN created_at TIMESTAMP(6) NOT NULL COMMENT '作业反馈创建时间';
