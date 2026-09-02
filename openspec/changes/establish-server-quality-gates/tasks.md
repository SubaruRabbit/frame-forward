## 1. Maven 质量门禁

- [x] 1.1 `frame-forward-server`：纳入受版本控制的 Eclipse JDT XML 与 Spotless 配置；验证 `mvn spotless:check` 可执行。
- [ ] 1.2 `frame-forward-server`：配置 JaCoCo XML、静态分析报告和核心系统阈值；以必填的 `QUALITY_BASE_REF` 比较 Git 基线并仅检查变更的可执行代码，验证失败会阻断构建；安全解析含标准 `report.dtd` 声明的 JaCoCo XML，且不得访问外部 DTD 或 schema；以测试先行拆分 `PasswordPolicy.validate` 和 `MediaService.ingest` 消除既有复杂度违规且不改变既有行为。
- [ ] 1.3 `frame-forward-server/bootstrap`：把测试、格式、覆盖率和分析命令汇总为可复现入口；验证全套命令通过。
- [ ] 1.4 `docs/constitution`：记录格式化范围、阈值口径和生成代码排除边界；人工审查配置可复现。
