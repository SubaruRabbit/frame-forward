## Why

拍摄方案接口要求持久化的 `sceneAnalysisId`，但现场分析任务结果仅向客户端返回 AI 输出，无法让 App 安全地衔接下一步方案生成。

## What Changes

- 在成功的现场分析任务结果中返回当前用户已拥有的 `sceneAnalysisId`。
- 将该字段写入 OpenAPI 契约，并由服务端集成测试验证。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `shooting/scene-analysis`: 成功结果提供可用于创建拍摄方案的持久化分析标识。

## Impact

影响现场分析服务、AI 任务结果、OpenAPI 契约和集成测试；App 可将该标识提交给既有 `/shooting-plans` 接口。

## Dependencies

- 既有 `shooting/plan-generation` 接口与 `ai/task-runtime` 状态查询。

## Non-goals

- 不改变现场分析输入、AI 结构化字段、拍摄方案规则或任务生命周期。
