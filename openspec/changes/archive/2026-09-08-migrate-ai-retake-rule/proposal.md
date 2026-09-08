## Why

继续已批准的 AI 模块分层，清理跨模块使用的重拍归因规则。

## What Changes

RetakeComparisonGraph 迁入 business，evaluation 生产和测试引用同步切换；不保留旧入口。

## Capabilities

纯分层重构，无业务变化。

## Impact

仅 ai-workflow、evaluation。

## Dependencies

依赖 migrate-ai-internal-layers，沿用已批准迁移范围。

## Non-goals

不改对比算法、响应、权限、持久化、公共任务入口，不提交发布。
