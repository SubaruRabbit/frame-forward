## Why

继续已确认的 Java 分层迁移，清理 AI 完成回调与实体边界。

## What Changes

两个回调实现及测试使用规范 gateway 和上下文 DTO，停止依赖 AI 持久化实体，保持事务和结果字段。

## Capabilities

纯分层重构，无业务变化。

## Impact

仅 generation、shooting。

## Dependencies

依赖 migrate-ai-completion-foundation，沿用用户已批准范围。

## Non-goals

不改 HTTP、状态机、权限、SQL、任务结果、调度或事务语义，不提交发布。
