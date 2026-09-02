## 1. portfolio 列表复杂度治理

- [x] 1.1 `frame-forward-server/portfolio`：增加测试范围依赖和 `PortfolioService.list` 特征测试，覆盖列表筛选、删除中作品排除和分页游标；验证相关 JUnit 测试通过且当前 PMD 复杂度检查按预期失败。
- [x] 1.2 `frame-forward-server/portfolio`：拆分 `PortfolioService.list` 的私有语义步骤；验证相关 JUnit 测试通过、该方法的 PMD 违规清除且复杂度不超过 8，并通过 portfolio 模块的测试、PMD、CPD 与 JaCoCo 门禁。
- [x] 1.3 `frame-forward-server`：执行 Spotless、完整测试与 PMD/CPD/JaCoCo 门禁；验证无行为回归，并清除 portfolio 复杂度变更导致的全量门禁阻断。
