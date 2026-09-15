## Context

见 `proposal.md`。拍摄域包含 `scene_analyses`、`shooting_plans` 两张表及两个 Entity。

## Goals / Non-Goals

**Goals:** 为两表及全部列补齐中文 COMMENT，并为拍摄 Entity 的每个字段增加独立中文 Javadoc。

**Non-Goals:** 不修改场景分析、计划生成、JSON 快照、约束或列定义。

## Decisions

1. 新增 `V23__document_shooting_schema.sql`，保留最终结构写入表和列 COMMENT。
2. `SceneAnalysisEntity`、`ShootingPlanEntity` 每个字段独占一行并紧邻中文 Javadoc。
3. 先增加失败测试，通过 `information_schema` 校验两表全列中文 COMMENT，并静态校验 Entity 字段布局与 Javadoc。

## Risks / Trade-offs

- [Risk] 字符集不同的 ID 列被统一而改变外键兼容性 → 保留各列现有字符集与排序规则并测试。
- [Risk] JSON 快照字段语义混淆 → 注释分别说明场景与器材快照来源。

## Migration Plan

随 Flyway 向前应用 V23；回退通过后续迁移清空 COMMENT。
