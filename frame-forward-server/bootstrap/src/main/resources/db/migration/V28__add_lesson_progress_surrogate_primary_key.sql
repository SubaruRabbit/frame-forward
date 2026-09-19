ALTER TABLE lesson_progress
  ADD COLUMN id CHAR(36) NULL FIRST;

UPDATE lesson_progress
SET id = UUID()
WHERE id IS NULL;

ALTER TABLE lesson_progress
  MODIFY COLUMN id CHAR(36) NOT NULL COMMENT '课程进度唯一标识',
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (id),
  ADD UNIQUE KEY uq_lesson_progress_account_version_lesson (account_id, content_version_id, lesson_id);
