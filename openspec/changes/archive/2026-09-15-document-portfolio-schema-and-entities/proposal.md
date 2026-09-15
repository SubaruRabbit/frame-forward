## Why

作品集域表和实体字段缺少中文语义说明，降低了收藏与删除任务数据的可读性。

## What Changes

- 为作品集域表及全部字段补充中文数据库描述。
- 为作品集域 Entity 的每个字段补充中文描述，并拆成一字段一行。

## Capabilities

不新增或修改运行时能力；本变更仅完善元数据和代码文档，已启用 `skip_specs`。

## Impact

影响 `bootstrap` 作品集迁移与 `portfolio` Entity；不改变表结构、接口或业务行为。

## Dependencies

依赖现有 Flyway 迁移顺序和 MyBatis Plus 映射。

## Non-goals

不改收藏、删除任务、约束或数据。
