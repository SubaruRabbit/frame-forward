## Context

见 `proposal.md`。生成域包含 `reference_images` 表和 `ReferenceImageEntity`。

## Goals / Non-Goals

**Goals:** 为参考图表及全部列补齐中文 COMMENT，并为实体全部字段增加逐字段中文 Javadoc。

**Non-Goals:** 不修改生成流程、提示词内容、媒体关联、约束或列定义。

## Decisions

1. 新增 `V25__document_generation_schema.sql`，保持当前结构设置表和列 COMMENT。
2. `ReferenceImageEntity` 每个字段独占一行并紧邻中文 Javadoc，保留现有注解和类型。
3. 先增加失败测试，通过 `information_schema` 校验全列中文 COMMENT，并静态校验 Entity 字段布局和 Javadoc。

## Risks / Trade-offs

- [Risk] 可空的 `generated_media_id` 被误改为非空 → 完整保留 NULL 属性并加入元数据断言。
- [Risk] 多个媒体/任务 ID 语义相近 → 中文注释明确输入、任务与生成结果关系。

## Migration Plan

随 Flyway 向前应用 V25；回退通过后续迁移清空 COMMENT。
