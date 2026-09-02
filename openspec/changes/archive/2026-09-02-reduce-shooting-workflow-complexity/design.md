## Context

`shooting` 是单一服务器模块。PMD 报告显示 `SceneAnalysisService.validate`（11）与 `ShootingPlanService.create`（9）超过复杂度上限 8。见 proposal.md 的动机与范围。

## Goals / Non-Goals

**Goals:**

- 将两处目标方法的圈复杂度降至不超过 8。
- 以单元或现有集成测试锁定成功、拒绝和边界语义。
- 保持分层、事务边界、幂等查询顺序及对外错误行为不变。

**Non-Goals:**

- 不引入新 Service、Business、Manager 层或公共 API。
- 不重写其他 shooting 逻辑，也不更改质量门禁配置。

## Decisions

### 私有语义步骤提取

对 `validate` 按请求字段族拆成私有布尔判断；对 `create` 按输入校验、所有权/上下文获取、已存在结果和新任务创建拆分。私有方法保留在原 Service，以避免公共接口和分层依赖变化。替代方案是提高阈值或压制 PMD，均违反质量门禁，故不采用。

### 先锁定行为再重构

先写覆盖当前成功、拒绝、缺失和边界路径的测试，再进行最小提取，最后执行模块和全量门禁。替代方案是只依赖 PMD 报告，无法证明重构未改变业务行为，故不采用。

## Risks / Trade-offs

- [提取改变判断顺序或异常类型] → 测试覆盖现有拒绝路径，保留原分支与查询顺序。
- [异步任务或幂等行为回归] → 保留实体构造、持久化和任务提交顺序，并执行现有集成测试。
- [新增测试改变格式] → 统一使用仓库 Eclipse JDT Spotless 配置验证。

## Migration Plan

无需数据迁移或发布切换。若出现回归，回滚这两个 Service 的私有提取及对应测试；不涉及数据结构。
