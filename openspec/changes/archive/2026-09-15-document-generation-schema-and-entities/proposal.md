## Why

参考图生成表和实体字段缺少中文语义说明，影响生成链路的数据排查效率。

## What Changes

- 为参考图生成表及全部字段补充中文数据库描述。
- 为生成域 Entity 的每个字段补充中文描述，并拆成一字段一行。

## Capabilities

不新增或修改运行时能力；本变更仅完善元数据和代码文档，已启用 `skip_specs`。

## Impact

影响 `bootstrap` 生成迁移与 `generation` Entity；不改变表结构、接口或生成行为。

## Dependencies

依赖现有 Flyway 迁移顺序和 MyBatis Plus 映射。

## Non-goals

不改提示词、图片生成流程、约束或数据。
