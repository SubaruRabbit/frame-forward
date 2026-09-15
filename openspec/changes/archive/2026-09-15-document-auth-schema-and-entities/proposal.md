## Why

认证域表和实体字段缺少中文语义说明，增加了数据库排查、评审和维护成本。

## What Changes

- 为认证域表及全部字段补充中文数据库描述。
- 为认证域 Entity 的每个字段补充中文描述，并拆成一字段一行。

## Capabilities

不新增或修改运行时能力；本变更仅完善元数据和代码文档，已启用 `skip_specs`。

## Impact

影响 `bootstrap` 认证迁移与 `auth` Entity；不改变表结构、接口或业务行为。

## Dependencies

依赖现有 Flyway 迁移顺序和 MyBatis Plus 映射。

## Non-goals

不改认证流程、约束、索引或数据。
