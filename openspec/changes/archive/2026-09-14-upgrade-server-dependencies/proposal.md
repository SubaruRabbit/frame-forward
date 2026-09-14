## Why

服务端仍使用 Spring Boot 3.5.8，且部分显式依赖、数据库迁移组件和质量插件已有兼容的新稳定版。统一升级并验证可减少已知缺陷与版本漂移风险。

## What Changes

- Spring Boot Parent 升级至 3.5.16。
- Flyway、MySQL 驱动与 Boot 3.5.16 BOM 对齐；其余自管依赖和质量插件升级至适合 Java 21 的稳定版本。
- 保持根 POM 集中版本管理，并执行全量 Maven 质量门禁。

## Capabilities

无产品行为或契约变更，`skip_specs: true`。

## Impact

影响 `frame-forward-server/pom.xml` 及所有继承模块的解析依赖和构建工具，不改变 API、业务逻辑或数据结构。

## Dependencies

依赖并保留 `centralize-server-maven-versions` 的集中版本管理结果。

## Non-goals

不升级到 Spring Boot 4、Flyway 13 或 MySQL Connector/J 26，不改 Java 格式化器和业务代码。
