## Why

继续已批准的 AI 公共任务入口分层迁移。

## What Changes

bootstrap 的生产/测试切换 service.AiTaskRuntime 和独立 model.dto 数据类型，不使用旧 Runtime 嵌套类型。

## Capabilities

纯重构，无业务变化。

## Impact

仅 bootstrap。

## Dependencies

依赖 migrate-ai-task-consumers-generation-evaluation，沿用用户已确认的迁移范围。

## Non-goals

不修改 AI 提供方、HTTP、JSON、状态、权限、事务、调度或数据库，不提交发布。
