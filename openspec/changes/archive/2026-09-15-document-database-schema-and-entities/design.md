## Context

见 `proposal.md`。AI 工作流域包含 `ai_tasks` 表和 `AiTaskEntity`，实体当前存在多字段同一声明。

## Goals / Non-Goals

**Goals:** 为任务表及全部列写入中文 COMMENT，并让实体每个字段独占一行且有中文 Javadoc。

**Non-Goals:** 不修改任务状态机、JSON 内容、列类型、约束或访问器行为。

## Decisions

1. 新增 `V21__document_ai_workflow_schema.sql`，以不改变最终定义的 DDL 设置表和列 COMMENT。
2. 拆分 `AiTaskEntity` 的聚合字段声明，每个字段紧邻中文 Javadoc，保留 Lombok 与 MyBatis Plus 配置。
3. 先增加失败测试，通过 `information_schema` 校验全列注释，并静态校验字段清单、逐行声明和中文 Javadoc。

## Risks / Trade-offs

- [Risk] 拆分声明时改变字段类型或遗漏字段 → 映射测试继续验证访问器，并新增显式字段清单断言。
- [Risk] COMMENT 迁移意外改变 JSON/时间精度 → `MODIFY COLUMN` 完整复用原定义并验证元数据。

## Migration Plan

随 Flyway 向前应用 V21；回退通过后续迁移清空 COMMENT。
