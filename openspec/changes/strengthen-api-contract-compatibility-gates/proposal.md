## Why

OpenAPI 校验有 8 项警告，且缺少兼容性门禁，无法证明契约变更不会破坏已发布客户端。

## What Changes

- 消除无依据的 OpenAPI 警告并建立兼容性检查。
- 对发现的行为/错误码差异另建带 delta spec 的 change。

## Capabilities

### New Capabilities

无。本 change 只治理契约质量，设置 `skip_specs: true`。

### Modified Capabilities

无。

## Impact

影响 `contracts` 的校验与文档元数据，不改变现有 API 语义。

## Dependencies

`isolate-server-tests-and-secure-configuration`、`establish-app-network-and-security-boundaries`。

## Non-goals

- 不凭空添加业务响应或错误码。
- 不改变服务端实现。
