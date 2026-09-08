## Context

按迁移清单的实体分批设计实施，当前只修改 shooting、generation。

## Goals / Non-Goals

生产与测试统一规范 MediaEntity，并使用 findOwnedEntity 查询，保持所有权、快照和生成行为。 不将本批当作整体迁移完成，不更改字段、表名、ID 或业务规则。

## Decisions

规范实体只承载原字段和构造器/getter。临时旧实体继承规范实体，保持旧返回类型与消费者 mock 兼容。规范查询返回基类，无泛型强转，委托既有 Repository。顺序为 foundation → consumers → finish；最后删除旧实体和旧 Manager 查询入口，规范查询保留，所有消费者及测试同步。

## Risks / Trade-offs

继承字段的 MyBatis 映射与包迁移可能回归；使用字段元数据断言、既有查询参数测试与完整 MySQL 集成测试证明映射、所有权和行为不变。各批先 Red 再 Green，禁止保留临时兼容入口作为最终结果。

## Migration Plan

依赖 migrate-media-entity-foundation。执行 Spotless apply/check 与 QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify。回滚逆序恢复本批类型/查询引用及路径，保留用户和前序修改并重跑门禁。media 完成后继续 ai-workflow、course、shooting、generation、evaluation、portfolio 的自身目录迁移。
