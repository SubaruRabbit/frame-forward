## Context

见 `proposal.md`。媒体域包含 `media` 表和 `MediaEntity`；历史迁移已被后续迁移调整列定义，必须以最终结构为准。

## Goals / Non-Goals

**Goals:** 补齐媒体表及所有列的中文 COMMENT，并为每个媒体实体字段增加独立中文 Javadoc。

**Non-Goals:** 不改变文件存储、列类型、外键、索引或映射行为。

## Decisions

1. 新增 `V19__document_media_schema.sql`，仅以保持最终定义的 DDL 写入表/列 COMMENT，不改历史迁移。
2. `MediaEntity` 每个字段单独一行、紧邻中文 Javadoc，保留类型与 MyBatis Plus 注解。
3. 先增加失败测试，查询 `information_schema` 验证 `media` 表与全部列的中文 COMMENT，并静态验证 Entity 字段布局和 Javadoc。

## Risks / Trade-offs

- [Risk] `owner_id` 最终字符集或外键属性被覆盖 → 依据 V16 后的最终结构编写完整列定义并测试。
- [Risk] `created_at` 未映射到 Entity 而被遗漏 → 数据库测试以表列清单为准，不以 Entity 字段推导。

## Migration Plan

随 Flyway 向前应用 V19；需要回退时用新迁移清空 COMMENT。
