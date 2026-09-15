## Why

拍摄域表和实体字段缺少中文语义说明，增加了场景分析与拍摄计划排查成本。

## What Changes

- 为拍摄域表及全部字段补充中文数据库描述。
- 为拍摄域 Entity 的每个字段补充中文描述，并拆成一字段一行。

## Capabilities

不新增或修改运行时能力；本变更仅完善元数据和代码文档，已启用 `skip_specs`。

## Impact

影响 `bootstrap` 拍摄迁移与 `shooting` Entity；不改变表结构、接口或生成行为。

## Dependencies

依赖现有 Flyway 迁移顺序和 MyBatis Plus 映射。

## Non-goals

不改场景分析、拍摄计划、约束或数据。
