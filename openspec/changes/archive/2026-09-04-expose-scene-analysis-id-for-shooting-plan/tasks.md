## 1. 现场分析结果契约

- [x] 1.1 `frame-forward-server/shooting`：先添加失败的集成测试，验证成功现场分析任务返回可用于 `/shooting-plans` 的 `sceneAnalysisId`。
- [x] 1.2 `frame-forward-server/shooting`：在完成结果中安全映射持久化的 `sceneAnalysisId`，并使集成测试通过。

## 2. 契约与质量验证

- [x] 2.1 `contracts`：更新 OpenAPI 及受控基线以声明成功现场分析结果的 `sceneAnalysisId`，并验证契约检查通过。
- [x] 2.2 `frame-forward-server`：执行 Spotless、Maven 测试与完整质量入口，验证新增可执行代码满足质量门禁。
