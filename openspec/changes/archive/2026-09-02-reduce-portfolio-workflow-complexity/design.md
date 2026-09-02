## Context

见 proposal.md。`PortfolioService.list` 在认证后逐项执行删除状态、游标、收藏和 EXIF 筛选，并生成汇总与分页游标；这些分支使方法复杂度达到 10。受影响模块为 `frame-forward-server/portfolio`。

## Goals / Non-Goals

**Goals:**

- 将目标方法复杂度降至不超过 8。
- 锁定列表筛选、删除中作品排除和分页游标的可观察行为。
- 保持认证与每项查询的相对顺序。

**Non-Goals:**

- 不将既有查询接口迁移至新的 Business 或 Manager 层。
- 不改变列表排序、筛选字段或页面大小默认值。

## Decisions

### 服务层特征测试

以真实 `PortfolioService` 测试列表返回结果和游标，隔离认证、查询接口与 Mapper。替代方案是 bootstrap 集成测试，但其依赖 Docker，不能快速保护重构。

### 提取列表语义步骤

将安全筛选条件、候选项判断、汇总收集和分页结果提取为私有方法。入口仅编排调用，以消除 PMD 违规；不引入新层或公共接口，避免扩大架构变更。

### PMD 作为重构 Red

先让特征测试覆盖当前行为，再保留 PMD 失败作为 Red，最小提取后验证测试和模块门禁转绿。

## Risks / Trade-offs

- [提取改变筛选或游标边界] → 以筛选、删除排除和下一页游标测试锁定返回结果。
- [替身不反映汇总依赖] → 使用真实 Service 与完整查询项夹具，断言对外返回字段。

## Migration Plan

无需迁移或发布切换。若发生回归，回滚 Service 私有提取、测试与测试范围依赖即可。
