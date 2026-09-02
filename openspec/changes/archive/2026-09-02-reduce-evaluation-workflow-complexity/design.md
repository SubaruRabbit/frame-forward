## Context

见 proposal.md。PMD 在 `PhotoEvaluationService.create` 与 `RetakeComparisonService.create` 报告方法圈复杂度违规。evaluation 模块当前仅有依赖 Docker 的 bootstrap 集成测试，缺少可在模块内快速执行的特征测试。

受影响项目与模块：`frame-forward-server/evaluation`。

## Goals / Non-Goals

**Goals:**

- 将两个目标方法的圈复杂度降至不超过 8。
- 保护创建成功、非法或无权上下文拒绝及幂等行为。
- 保持原始查询、任务创建和持久化顺序。

**Non-Goals:**

- 不变更评测或重拍对比的请求、响应和错误契约。
- 不迁移到 Business/Manager 分层。

## Decisions

### 模块级特征测试

为 evaluation 添加测试范围的 Spring Boot Test 依赖，通过两个 `create` 入口测试可观察的异常、返回任务及持久化副作用。仅隔离 Mapper、认证和任务运行时等外部协作者。替代方案是只使用 bootstrap 集成测试，但其依赖 Docker，不能快速验证重构。

### 语义私有步骤提取

每个 `create` 按输入校验、归属上下文加载、幂等结果检查、任务提交和结果持久化提取私有步骤；维持原 Service、事务边界和调用顺序。替代方案是提高阈值或压制 PMD，违反质量门禁。

### PMD 作为重构 Red

先以特征测试锁定现有行为，再保留当前 PMD 失败作为重构 Red；最小提取后验证测试和 PMD 转绿。

## Risks / Trade-offs

- [提取改变异常或查询顺序] → 用无效、无权和幂等路径测试锁定外部行为。
- [替身遗漏真实持久化副作用] → 断言任务返回与保存实体的业务字段，保留 bootstrap 集成测试作最终门禁。
- [新增测试依赖扩大运行时产物] → 限定为 `test` scope。

## Migration Plan

无需数据迁移或发布切换。若出现回归，回滚两个 Service 的私有提取、测试和测试依赖即可。
