## Why

media、ai-workflow 的业务和持久化职责混杂，缺少可检查分层。

## What Changes

- 将两模块迁移到规定的 Business/Manager 边界。

## Capabilities

### New Capabilities

无；内部重构，`skip_specs: true`。

### Modified Capabilities

无。

## Impact

仅 media、ai-workflow 内部实现与测试。

## Dependencies

C0、C1。

## Non-goals

不改变媒体或 AI 任务契约。
