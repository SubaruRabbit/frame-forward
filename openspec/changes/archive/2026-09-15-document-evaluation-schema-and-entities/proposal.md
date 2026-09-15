## Why

评估域表和实体字段缺少中文语义说明，增加了评分与重拍链路的理解成本。

## What Changes

- 为评估域表及全部字段补充中文数据库描述。
- 为评估域 Entity 的每个字段补充中文描述，并拆成一字段一行。

## Capabilities

不新增或修改运行时能力；本变更仅完善元数据和代码文档，已启用 `skip_specs`。

## Impact

影响 `bootstrap` 评估迁移与 `evaluation` Entity；不改变表结构、接口或评分行为。

## Dependencies

依赖现有 Flyway 迁移顺序和 MyBatis Plus 映射。

## Non-goals

不改评分规则、重拍关系、约束或数据。
