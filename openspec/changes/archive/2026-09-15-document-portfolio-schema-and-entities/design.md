## Context

见 `proposal.md`。作品集域包含 `portfolio_favorites`、`portfolio_work_deletion_jobs` 两张表和两个 Entity。

## Goals / Non-Goals

**Goals:** 为两表全列补齐中文 COMMENT，并为作品集 Entity 全部字段增加逐字段中文 Javadoc。

**Non-Goals:** 不修改收藏、删除任务状态、级联关系、约束或列定义。

## Decisions

1. 新增 `V26__document_portfolio_schema.sql`，保持最终结构设置两表及全部列 COMMENT。
2. `PortfolioFavoriteEntity`、`PortfolioWorkDeletionJobEntity` 每个字段独占一行并紧邻中文 Javadoc。
3. 先增加失败测试，通过 `information_schema` 校验两表全列中文 COMMENT，并静态校验 Entity 字段布局和 Javadoc。

## Risks / Trade-offs

- [Risk] 删除任务可空失败原因被误改 → 完整保留 NULL 属性并测试。
- [Risk] 收藏复合键列遗漏 → 明确校验 `media_id`、`account_id`、`created_at` 全部列。

## Migration Plan

随 Flyway 向前应用 V26；回退通过后续迁移清空 COMMENT。
