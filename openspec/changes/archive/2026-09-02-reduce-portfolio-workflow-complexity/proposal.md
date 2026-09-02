## Why

服务器全量质量门禁在 evaluation 修复后发现 `PortfolioService.list` 圈复杂度为 10，超过核心系统上限 8，持续阻断验证。

## What Changes

- 为作品集列表补充成功、筛选、删除中作品排除与分页的特征测试。
- 将列表流程提取为私有语义步骤，保持认证、过滤、收藏查询、汇总与游标计算顺序。

## Capabilities

### New Capabilities

无。本变更只进行内部等价重构。

### Modified Capabilities

无。对外需求与契约不变。

## Dependencies

- `establish-server-quality-gates` 的 PMD 圈复杂度门禁。
- 已有 portfolio、media 与 evaluation 查询接口。

## Non-goals

- 不修改作品集 API、筛选规则、游标规则、数据模型或 PMD 阈值。
- 不处理其他模块的质量门禁问题。

## Impact

仅影响 `frame-forward-server/portfolio` 的测试依赖、`PortfolioService` 和模块内单元测试。
