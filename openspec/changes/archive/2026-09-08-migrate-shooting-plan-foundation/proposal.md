## Why

继续已确认的剩余模块 Java 分层迁移。

## What Changes

建立规范 model.entity.ShootingPlanEntity 和 mapper.ShootingPlanMapper，旧实体暂继承规范实体，旧 Mapper 使用明确 legacy bean 名避免扫描冲突。

## Capabilities

无业务能力变化，纯重构。

## Impact

仅 shooting。

## Dependencies

依赖 已完成 evaluation 迁移，沿用用户已批准的迁移范围。

## Non-goals

不改变 HTTP、字段、表结构、任务输入、权限、事务和查询条件，不提交发布。
