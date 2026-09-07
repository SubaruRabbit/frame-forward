## Why

portfolio、reference-image 页面直接访问 fetch 和凭据，违反 Presentation 边界。

## What Changes

- 将两 Feature 接入 Application/Port 与统一网络层。

## Capabilities

### New Capabilities

无；内部重构，`skip_specs: true`。

### Modified Capabilities

无。

## Impact

仅两个 APP Feature 的内部结构与测试。

## Dependencies

C2、C3。

## Non-goals

不改变页面用户流程或 API 契约。
