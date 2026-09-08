## Why

消费者已切换，继续已批准的 AI 公共任务入口收尾，删除过渡实现。

## What Changes

实际 Runtime 迁入 service，Business/Controller/异常映射归层；独立业务异常与 Creation 数据记录，认证回归 Service，删除旧 Runtime 和委托。

## Capabilities

纯分层重构，无业务变化。

## Impact

仅 ai-workflow。

## Dependencies

依赖 migrate-ai-task-consumer-bootstrap；全部外部消费者已使用规范类型。

## Non-goals

不改 HTTP、状态机、调度、事务、认证顺序、SQL、回调或实体，不提交发布。
