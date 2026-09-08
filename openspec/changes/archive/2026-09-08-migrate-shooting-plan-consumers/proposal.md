## Why

继续已确认的剩余模块 Java 分层迁移。

## What Changes

两个消费者及测试切换规范 ShootingPlanEntity/Mapper 引用，不改变查询及业务行为。

## Capabilities

无业务能力变化，纯重构。

## Impact

仅 generation、evaluation。

## Dependencies

依赖 migrate-shooting-plan-foundation，沿用用户已批准的迁移范围。

## Non-goals

不改变 HTTP、字段、表结构、任务输入、权限、事务和查询条件，不提交发布。
