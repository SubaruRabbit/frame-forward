## Why

新增 PMD 复杂度门禁发现 `ai-workflow` 存在五处既有方法超限，导致服务端质量构建被阻断。

## What Changes

- 以测试先行方式拆分五处超限方法，使每个方法圈复杂度不高于 8。
- 保持 AI 任务状态、输入输出、验证规则和恢复语义不变。

## Capabilities

### New Capabilities

无；内部重构，`skip_specs: true`。

### Modified Capabilities

无。

## Impact

仅影响 `frame-forward-server/ai-workflow` 的内部实现和测试。

## Dependencies

`establish-server-quality-gates`、`isolate-server-tests-and-secure-configuration`。

## Non-goals

- 不改变 AI 任务 API、状态机或模型路由。
- 不调整 PMD 阈值或排除现有生产代码。
