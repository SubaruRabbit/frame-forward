## 1. ai-workflow 复杂度治理

- [x] 1.1 `frame-forward-server/ai-workflow`：为 `AiTaskRuntime.create` 补充创建、失败与恢复回归测试并拆分私有步骤；验证相关 JUnit 测试和 PMD 复杂度检查通过。
- [x] 1.2 `frame-forward-server/ai-workflow`：为 `CourseGenerationValidator.valid` 补充有效、缺失和类型错误输入测试并拆分校验步骤；验证相关 JUnit 测试和 PMD 复杂度检查通过。
- [x] 1.3 `frame-forward-server/ai-workflow`：为 `PhotoEvaluationGraph.execute` 与 `valid` 补充成功、无效输出和边界输入测试并拆分执行/校验步骤；验证相关 JUnit 测试和 PMD 复杂度检查通过。
- [x] 1.4 `frame-forward-server/ai-workflow`：为 `PlanGenerationGraph.completeAndSafe` 补充完整、安全与缺失字段测试并拆分私有判断；验证相关 JUnit 测试和 PMD 复杂度检查通过。
- [x] 1.5 `frame-forward-server`：执行 Spotless、完整测试与 PMD/CPD/JaCoCo 质量门禁；验证五个方法复杂度均不超过 8 且无行为回归。
