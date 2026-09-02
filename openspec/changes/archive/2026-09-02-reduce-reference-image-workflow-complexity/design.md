## Context

见 proposal.md。当前 `ReferenceImageService.validate` 将顶层标识、方案文本字段和结构化字段集中在一个短路条件中，PMD 报告圈复杂度为 11。generation 模块没有独立单元测试依赖，仅由需要 Docker 的 bootstrap 集成测试间接覆盖。

受影响项目与模块：`frame-forward-server/generation`。

## Goals / Non-Goals

**Goals:**

- 将目标方法圈复杂度降至不超过 8。
- 锁定有效请求、缺失字段和字段类型错误的拒绝语义。
- 保持认证、所有权查询、任务提交和持久化顺序不变。

**Non-Goals:**

- 不改变公开 API、异常映射或参考图生成提示词。
- 不执行 generation/evaluation 分层迁移。

## Decisions

### 增加模块级特征测试

为 generation 增加测试范围的 Spring Boot Test 依赖，通过 `create` 的真实入口验证请求行为，仅隔离 Mapper、认证和任务运行时等外部协作者。测试关注返回结果或异常及关键副作用，不测试私有方法。替代方案是仅依赖 bootstrap 集成测试，但其依赖 Docker，无法提供快速、隔离的重构反馈。

### 按字段族提取私有判断

把顶层标识、方案文本字段和结构化字段分别提取为私有布尔判断，`validate` 只组合这些结果并继续抛出同一 `InvalidRequest`。保留原有短路顺序。替代方案是提高阈值或压制 PMD，均违反质量门禁。

### 以 PMD 违规作为重构 Red

先建立通过的行为特征测试，再确认当前 PMD 因复杂度失败；随后进行最小提取并验证行为测试持续通过、PMD 转绿。复杂度是本次重构需要改变的唯一可观察工程属性。

## Risks / Trade-offs

- [字段分组改变短路或空值安全] → 按原顺序组合判断，并覆盖 null、空白和错误类型。
- [测试替身遗漏真实副作用] → 仅在请求边界做单元特征测试，同时保留 bootstrap 集成测试作为最终门禁。
- [新增测试依赖扩大运行时产物] → 依赖限定为 `test` scope。

## Migration Plan

无需数据迁移或发布切换。若出现回归，回滚私有提取、单元测试与测试依赖即可。
