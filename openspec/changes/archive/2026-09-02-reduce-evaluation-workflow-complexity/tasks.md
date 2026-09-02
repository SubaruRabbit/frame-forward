## 1. evaluation 模块复杂度治理

- [x] 1.1 `frame-forward-server/evaluation`：增加测试范围依赖和 `PhotoEvaluationService` 特征测试，覆盖创建、拒绝与幂等；验证相关 JUnit 测试通过且当前 PMD 复杂度检查按预期失败。
- [x] 1.2 `frame-forward-server/evaluation`：拆分 `PhotoEvaluationService.create` 的私有语义步骤；验证相关 JUnit 测试通过、该方法的 PMD 违规清除且复杂度不超过 8，允许 `RetakeComparisonService` 的既有违规暂存至 1.4。
- [x] 1.3 `frame-forward-server/evaluation`：补充 `RetakeComparisonService` 的创建、拒绝与幂等特征测试；验证相关 JUnit 测试通过且当前 PMD 复杂度检查按预期失败。
- [x] 1.4 `frame-forward-server/evaluation`：拆分 `RetakeComparisonService.create` 的私有语义步骤；验证相关 JUnit 测试通过、两个目标方法的 PMD 违规均清除且 evaluation PMD 检查通过，目标方法复杂度不超过 8。
- [x] 1.5 `frame-forward-server`：执行 Spotless、完整测试与 PMD/CPD/JaCoCo 门禁；验证无行为回归，并清除上游复杂度变更的全量门禁阻断。
