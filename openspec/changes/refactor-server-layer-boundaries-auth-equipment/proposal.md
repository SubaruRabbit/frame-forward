## Why

auth、equipment 的 Service 直接持久化并承载复合规则，违反后端分层边界。

## What Changes

- 将复杂规则移至 Business，将持久化编排移至 Manager。

## Capabilities

### New Capabilities

无；内部重构，`skip_specs: true`。

### Modified Capabilities

无。

## Impact

仅 auth、equipment 内部实现与测试。

## Dependencies

C0、C1。

## Non-goals

不改变 API、认证或器材规则。
