## Context

见 `proposal.md`。认证域包含 `accounts`、`refresh_sessions`、`account_deletion_jobs` 三张表和三个 Entity；已发布的 Flyway 迁移不可改写校验和。

## Goals / Non-Goals

**Goals:** 以向前迁移补齐三张表及所有列的中文 COMMENT，并以字段级中文 Javadoc 覆盖全部认证 Entity 字段。

**Non-Goals:** 不改变列定义、约束、索引、映射可见性或运行时行为。

## Decisions

1. 新增 `V18__document_auth_schema.sql`，使用 `ALTER TABLE ... COMMENT` 和保持原定义的 `MODIFY COLUMN ... COMMENT`。不编辑历史迁移，避免 Flyway 校验和失配。
2. `AccountEntity`、`RefreshSessionEntity`、`AccountDeletionJobEntity` 的每个字段单独声明并紧邻中文 Javadoc；保留现有注解和类型。
3. 先增加失败测试：通过 MySQL `information_schema` 验证表/列 COMMENT 非空且含中文，并以源码检查验证每个实体字段独占一行且有中文 Javadoc。

## Risks / Trade-offs

- [Risk] `MODIFY COLUMN` 遗漏原有属性会改变结构 → 从最终迁移状态逐列复制完整定义，并由迁移测试比对关键属性。
- [Risk] 注释迁移遗漏表或列 → 测试按显式清单校验全部三张表及其列。

## Migration Plan

随 Flyway 向前应用 V18；回滚时新增反向迁移清空 COMMENT，禁止修改已执行脚本。
