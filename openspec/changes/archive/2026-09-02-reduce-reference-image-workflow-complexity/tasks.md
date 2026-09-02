## 1. generation 模块复杂度治理

- [x] 1.1 `frame-forward-server/generation`：增加测试范围依赖和 `ReferenceImageService` 特征测试，覆盖有效请求、缺失字段与错误字段类型；验证相关 JUnit 测试通过且当前 PMD 复杂度检查按预期失败。
- [x] 1.2 `frame-forward-server/generation`：按顶层标识、方案文本字段和结构化字段拆分 `validate` 私有判断；验证相关 JUnit 测试与 PMD 检查通过，目标方法复杂度不超过 8。
- [x] 1.3 `frame-forward-server`：执行 Spotless、完整测试与 PMD/CPD/JaCoCo 门禁；验证无行为回归，并为 `reduce-shooting-workflow-complexity` 清除全量门禁阻断。
