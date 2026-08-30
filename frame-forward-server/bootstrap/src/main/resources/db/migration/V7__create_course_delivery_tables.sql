CREATE TABLE course_content_versions (
  id VARCHAR(36) PRIMARY KEY,
  course_id VARCHAR(80) NOT NULL,
  content_version VARCHAR(80) NOT NULL,
  model_id VARCHAR(80) NOT NULL,
  prompt_version VARCHAR(80) NOT NULL,
  source_material_version VARCHAR(80) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  UNIQUE KEY uq_course_content_version (course_id, content_version)
);
CREATE TABLE lesson_progress (
  account_id VARCHAR(36) NOT NULL,
  content_version_id VARCHAR(36) NOT NULL,
  lesson_id VARCHAR(80) NOT NULL,
  completed_at TIMESTAMP(6) NOT NULL,
  PRIMARY KEY (account_id, content_version_id, lesson_id),
  CONSTRAINT fk_progress_course_version FOREIGN KEY (content_version_id) REFERENCES course_content_versions(id)
);
CREATE TABLE lesson_assignment_feedback (
  id VARCHAR(36) PRIMARY KEY,
  account_id VARCHAR(36) NOT NULL,
  content_version_id VARCHAR(36) NOT NULL,
  lesson_id VARCHAR(80) NOT NULL,
  media_id VARCHAR(36) NOT NULL,
  feedback_task_id VARCHAR(36) NOT NULL,
  lesson_objective VARCHAR(500) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  UNIQUE KEY uq_assignment_media_lesson (account_id, content_version_id, lesson_id, media_id)
);
