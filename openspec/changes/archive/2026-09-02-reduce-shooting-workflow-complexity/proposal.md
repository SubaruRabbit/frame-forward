## Why

服务器质量门禁已识别出 shooting 模块两处方法的圈复杂度超过核心系统上限 8，阻断完整验证。需在不改变拍摄场景校验和拍摄计划创建行为的前提下消除违规。

## What Changes

- 为 `SceneAnalysisService.validate` 拆分请求字段校验并补充正常、缺失与边界输入测试。
- 为 `ShootingPlanService.create` 拆分创建前置校验、上下文加载和任务提交步骤并补充创建、拒绝与幂等测试。
- 保持现有 API、数据库结构、任务模型、状态码和质量阈值不变。

## Capabilities

### New Capabilities

无。本变更仅为内部等价重构，不引入可观察行为。

### Modified Capabilities

无。对外需求与契约不变。

## Dependencies

- `establish-server-quality-gates` 的 PMD 复杂度门禁。
- 已存在的 shooting 模块单元与集成测试。

## Non-goals

- 不修改 OpenAPI、Controller、数据表、AI 工作流路由或 PMD/覆盖率阈值。
- 不治理 shooting 模块以外的代码。

## Impact

影响 `frame-forward-server/shooting` 中的两个 Service 及其测试；无外部接口或依赖变更。
