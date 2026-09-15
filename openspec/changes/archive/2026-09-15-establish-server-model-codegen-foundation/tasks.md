## 1. 编译基线

- [x] 1.1 在 frame-forward-server 增加 Lombok 与 MapStruct 协作的失败编译测试，并验证当前构建因缺少生成成员或实现而失败
- [x] 1.2 在 frame-forward-server 父 POM 固定 Lombok、MapStruct、binding 和 compiler plugin 版本，并验证 Maven effective POM 包含预期配置
- [x] 1.3 在 frame-forward-server 配置 annotation processor paths、Spring component model 和严格未映射策略，并验证编译测试通过

## 2. 基础验证

- [x] 2.1 在 frame-forward-server 执行 `mvn spotless:apply` 与 `mvn spotless:check`，验证新增 POM 和测试格式合规
- [x] 2.2 在 frame-forward-server 执行 `mvn test` 及 `QUALITY_BASE_REF=HEAD mvn verify`，验证 Reactor 和质量报告门禁通过
