## Context

见 proposal.md。PMD 7.7.0 报告 `ai-workflow` 的五个既有方法圈复杂度为 10–19，高于核心系统上限 8；该模块已存在对应单元测试和集成测试。

## Goals / Non-Goals

**Goals:** 以不改变既有 AI 任务行为的私有方法提取，将每个受影响方法降至复杂度 8 或以下，并以回归测试与 PMD 验证。

**Non-Goals:** 不迁移分层职责、不修改数据库或 API 契约、不调整质量阈值。

## Decisions

- 仅处理 PMD 当前报告的五个方法：`AiTaskRuntime.create`、`CourseGenerationValidator.valid`、`PhotoEvaluationGraph.execute`、`PhotoEvaluationGraph.valid`、`PlanGenerationGraph.completeAndSafe`。原因是门禁失败由这些确定证据引起；不借机重构其他代码。
- 每个方法先补正常、异常和边界路径测试，再提取命名明确的私有校验、状态转换或结果组装步骤。相比修改 PMD 配置，这保留核心系统复杂度标准并锁定行为。
- 保持现有公开方法签名和异常/状态结果；只在类内重组流程。相比新增跨类抽象，这一方式不扩大模块边界或引入依赖风险。

## Risks / Trade-offs

- [异步任务状态回归] → 覆盖创建、失败、恢复和重试的既有测试。
- [模型输出校验回归] → 覆盖有效、缺失和类型错误输入。
- [遗漏既有违规] → 完成后执行完整 PMD 报告和服务端质量门禁。

## Migration Plan

逐方法测试先行、拆分并验证；无需数据迁移。若出现行为回归，回滚相应私有方法提取且保留测试证据。
