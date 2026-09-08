## Why

继续已确认迁移，为公共 AI 任务入口建立规范 Service 和独立 DTO，支持消费者分批切换。

## What Changes

新增 service.AiTaskRuntime 与五个 model.dto 数据类型，暂委托现有 Runtime，仅转换 Java 数据类型，不复制业务规则。

## Capabilities

纯分层迁移，无业务变化。

## Impact

仅 ai-workflow。

## Dependencies

依赖 migrate-ai-retake-rule；后续消费者按 course/shooting、generation/evaluation、bootstrap 分批切换，再由 AI 单模块迁移实际实现并删除过渡委托和旧嵌套类型。

## Non-goals

不改消费者、HTTP、JSON、认证顺序、状态机、SSE、调度、事务、SQL，不提交发布。
