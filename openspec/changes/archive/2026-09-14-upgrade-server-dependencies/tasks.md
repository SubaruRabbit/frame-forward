## 1. 版本升级

- [x] 1.1 在 `frame-forward-server` 根 POM 将 Spring Boot Parent、Flyway、MySQL Connector/J、metadata-extractor、JaCoCo 和 PMD 更新为设计目标版本，并用 `rg` 确认子模块没有新增或残留显式依赖版本

## 2. 依赖解析验证

- [x] 2.1 在 `frame-forward-server` 生成 effective POM 和依赖树，确认全部模块继承 Spring Boot 3.5.16，Flyway 解析为 11.7.2、MySQL Connector/J 为 9.7.0、metadata-extractor 为 2.21.0，且没有意外的里程碑或快照版本

## 3. 质量门禁

- [x] 3.1 在 `frame-forward-server` 执行 `mvn spotless:apply` 与 `mvn spotless:check`，并检查差异确认未因本次升级产生业务代码或格式化器变更
- [x] 3.2 在 `frame-forward-server` 执行 `QUALITY_BASE_REF=HEAD mvn -q -DskipTests=false verify`，确认 Reactor 编译、静态检查、单元测试、集成测试和覆盖率门禁全部通过

## 4. 交付审查

- [x] 4.1 审查 `frame-forward-server` POM 差异并运行 `openspec validate upgrade-server-dependencies --strict`，确认只包含已批准的版本升级且 OpenSpec 变更有效
