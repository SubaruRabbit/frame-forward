## Why

继续已确认的 Java 分层迁移，清理 AI 完成回调与实体边界。

## What Changes

删除旧回调桥接，AiTaskEntity 迁入 model.entity，启用全模块目录零违规和架构依赖检查。

## Capabilities

纯分层重构，无业务变化。

## Impact

仅 ai-workflow。

## Dependencies

依赖 migrate-ai-completion-consumers，沿用用户已批准范围。

## Non-goals

不改 HTTP、状态机、权限、SQL、任务结果、调度或事务语义，不提交发布。
