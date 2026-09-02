## Why

服务器完整质量门禁在 generation 修复后发现 evaluation 模块两处 `create` 方法超过核心系统圈复杂度上限 8，继续阻断全量验证。需在不改变评测与重拍对比行为的前提下消除违规。

## What Changes

- 为照片评测和重拍对比创建流程补充成功、拒绝与幂等特征测试。
- 将两个 `create` 方法拆分为私有语义步骤，保持查询、任务提交和持久化顺序。
- 重新执行模块与服务器质量门禁。

## Capabilities

### New Capabilities

无。本变更仅为内部等价重构。

### Modified Capabilities

无。对外需求与契约不变。

## Dependencies

- `establish-server-quality-gates` 的 PMD 复杂度门禁。
- 已有 evaluation 服务与 bootstrap 集成测试。

## Non-goals

- 不修改 OpenAPI、状态码、数据库、任务模型或阈值。
- 不执行 generation/evaluation 的分层迁移。

## Impact

仅影响 `frame-forward-server/evaluation` 的测试依赖、两个 Service 及其单元测试。
