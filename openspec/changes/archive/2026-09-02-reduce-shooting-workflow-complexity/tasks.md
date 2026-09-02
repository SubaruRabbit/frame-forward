## 1. shooting 模块复杂度治理

- [x] 1.1 `frame-forward-server/shooting`：为 `SceneAnalysisService.validate` 补充正常、缺失和边界请求测试并拆分私有字段校验；验证相关 JUnit 测试和 PMD 复杂度检查通过。
- [x] 1.2 `frame-forward-server/shooting`：为 `ShootingPlanService.create` 补充创建、拒绝与幂等回归测试并拆分前置校验、上下文加载和任务提交步骤；验证相关 JUnit 测试和 PMD 复杂度检查通过。
- [x] 1.3 `frame-forward-server`：执行 Spotless、完整测试与 PMD/CPD/JaCoCo 门禁；验证两个目标方法复杂度均不超过 8 且无行为回归。
