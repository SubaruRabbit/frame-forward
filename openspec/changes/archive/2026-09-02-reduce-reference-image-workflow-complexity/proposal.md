## Why

服务器完整质量门禁发现 `ReferenceImageService.validate` 圈复杂度超过核心系统上限 8，阻断 shooting 变更的全量验证。需在不改变参考图请求校验行为的前提下消除违规。

## What Changes

- 为参考图请求补充有效、缺失和错误类型输入测试。
- 将聚合校验拆为私有字段族判断，保持判断顺序与异常类型不变。
- 执行 generation 模块及服务器质量门禁。

## Capabilities

### New Capabilities

无。本变更仅为内部等价重构。

### Modified Capabilities

无。对外需求与契约不变。

## Dependencies

- `establish-server-quality-gates` 的 PMD 复杂度门禁。
- 现有参考图创建与集成测试。

## Non-goals

- 不修改 OpenAPI、状态码、数据库结构、任务模型或质量阈值。
- 不处理 generation 的分层迁移或其他模块复杂度。

## Impact

仅影响 `frame-forward-server/generation` 的测试依赖、`ReferenceImageService` 及其单元测试。
