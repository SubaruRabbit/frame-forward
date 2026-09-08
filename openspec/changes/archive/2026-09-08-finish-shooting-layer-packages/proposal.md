## Why

继续已确认的剩余模块 Java 分层迁移。

## What Changes

删除临时根包实体和 Mapper；其余生产类型分包，提取 DTO/异常，Repository 隔离数据访问，目录零违规。

## Capabilities

无业务能力变化，纯重构。

## Impact

仅 shooting。

## Dependencies

依赖 migrate-shooting-plan-consumers，沿用用户已批准的迁移范围。

## Non-goals

不改变 HTTP、字段、表结构、任务输入、权限、事务和查询条件，不提交发布。
