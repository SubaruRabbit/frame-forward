## 1. Maven 质量门禁

- [ ] 1.1 `frame-forward-server`：纳入可复现 IntelliJ Code Style 与 Spotless 配置；验证 `mvn spotless:check` 可执行。
- [ ] 1.2 `frame-forward-server`：配置 JaCoCo 和静态分析报告及核心系统阈值；验证失败会阻断构建。
- [ ] 1.3 `frame-forward-server/bootstrap`：把测试、格式、覆盖率和分析命令汇总为可复现入口；验证全套命令通过。
- [ ] 1.4 `docs/constitution`：记录格式化范围、阈值口径和生成代码排除边界；人工审查配置可复现。
