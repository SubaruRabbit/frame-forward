## Context

见 `proposal.md`。评估域包含 `evaluation_rules`、`photo_evaluations`、`shooting_sessions`、`retake_links` 四张表和三个 Entity。

## Goals / Non-Goals

**Goals:** 为四表全列补齐中文 COMMENT，并为评估 Entity 的全部字段增加逐字段中文 Javadoc。

**Non-Goals:** 不修改评分权重种子、会话/重拍规则、外键、类型或业务逻辑。

## Decisions

1. 新增 `V24__document_evaluation_schema.sql`，以当前最终结构描述四张表及全部列。
2. `PhotoEvaluationEntity`、`ShootingSessionEntity`、`RetakeLinkEntity` 每个字段独占一行并紧邻中文 Javadoc。
3. 先增加失败测试，通过 `information_schema` 校验四表全列中文 COMMENT，并静态校验三个 Entity 的字段布局与 Javadoc。

## Risks / Trade-offs

- [Risk] V13 对 `photo_evaluations` 的新增列被遗漏 → 测试以最终全列清单覆盖 `shooting_session_id`。
- [Risk] 规则表无 Entity 导致误漏 → 数据库覆盖独立于 Entity 清单验证。

## Migration Plan

随 Flyway 向前应用 V24；回退通过后续迁移清空 COMMENT。
