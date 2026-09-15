## Context

见 `proposal.md`。课程域包含三张交付表和三个 Entity，账号列定义已在 V17 对齐。

## Goals / Non-Goals

**Goals:** 为三张表及全部列补齐中文 COMMENT，并为课程 Entity 的所有字段增加逐字段中文 Javadoc。

**Non-Goals:** 不修改课程版本、进度、作业反馈规则、外键或列定义。

## Decisions

1. 新增 `V22__document_course_schema.sql`，以 V17 后最终结构为准描述 `course_content_versions`、`lesson_progress`、`lesson_assignment_feedback`。
2. `CourseContentVersionEntity`、`LessonProgressEntity`、`AssignmentFeedbackEntity` 每个字段单独一行并紧邻中文 Javadoc。
3. 先增加失败测试，通过 `information_schema` 校验三表全列中文 COMMENT，并静态校验 Entity 字段布局和 Javadoc。

## Risks / Trade-offs

- [Risk] V17 调整后的字符集或外键被旧定义覆盖 → 从最终状态复制列定义并测试账号列元数据。
- [Risk] 复合主键字段遗漏 → 测试逐列覆盖且保留现有映射注解。

## Migration Plan

随 Flyway 向前应用 V22；回退通过后续迁移清空 COMMENT。
