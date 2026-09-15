## Why

课程交付域表和实体字段缺少中文语义说明，影响数据理解与维护效率。

## What Changes

- 为课程交付域表及全部字段补充中文数据库描述。
- 为课程域 Entity 的每个字段补充中文描述，并拆成一字段一行。

## Capabilities

不新增或修改运行时能力；本变更仅完善元数据和代码文档，已启用 `skip_specs`。

## Impact

影响 `bootstrap` 课程迁移与 `course` Entity；不改变表结构、接口或业务行为。

## Dependencies

依赖现有 Flyway 迁移顺序和 MyBatis Plus 映射。

## Non-goals

不改课程内容、学习进度规则、约束或数据。
