## Why

AI 工作流任务表和实体字段缺少中文语义说明，增加了任务状态排查成本。

## What Changes

- 为 AI 任务表及全部字段补充中文数据库描述。
- 为 AI 任务 Entity 的每个字段补充中文描述，并拆成一字段一行。

## Capabilities

不新增或修改运行时能力；本变更仅完善元数据和代码文档，已启用 `skip_specs`。

## Impact

影响 `bootstrap` AI 迁移与 `ai-workflow` Entity；不改变表结构、接口或任务行为。

## Dependencies

依赖现有 Flyway 迁移顺序和 MyBatis Plus 映射。

## Non-goals

不改工作流、状态机、模型路由、约束或数据。
