## Why

继续已确认的 Java 分层迁移，清理 AI 完成回调与实体边界。

## What Changes

建立 gateway.AiTaskCompletionProcessor 和只含 taskId/accountId 的 model.dto.AiTaskCompletionContext；回调协调迁入 Manager，旧回调暂桥接新上下文。

## Capabilities

纯分层重构，无业务变化。

## Impact

仅 ai-workflow。

## Dependencies

依赖 finish-ai-task-service-package，沿用用户已批准范围。

## Non-goals

不改 HTTP、状态机、权限、SQL、任务结果、调度或事务语义，不提交发布。
