## Why

APP 页面直接调用 `fetch`、读取凭据并硬编码地址，缺少统一错误、超时和环境边界。

## What Changes

- 建立 Network Layer、环境配置、错误映射与安全存储 Port。
- 保持现有 API 语义，将基础设施依赖移出 Presentation。

## Capabilities

### New Capabilities

无。本 change 只调整内部架构，设置 `skip_specs: true`。

### Modified Capabilities

无。

## Impact

影响 `app`、`shared`、现有 Feature 调用点及测试，不改页面功能或公开契约。

## Dependencies

合规基线 C2。

## Non-goals

- 不新增业务功能或 SDK。
- 不把 Token 写入普通存储或日志。
