## Why

服务端各模块 POM 分散声明第三方依赖与插件版本，升级时容易遗漏并产生不一致。需要统一由根 POM 的 `properties` 管理版本，同时保持当前 Maven 解析结果不变。

## What Changes

- 为显式依赖、插件及插件内依赖版本定义根属性。
- 在根 POM 使用 `dependencyManagement` 管理内部模块、MyBatis Plus 和 metadata-extractor，子模块省略对应版本。
- 插件及插件内依赖继续引用根属性，保持项目坐标不变。

## Capabilities

本次为纯构建配置重构，设置 `skip_specs: true`，不新增或修改产品能力规格。

## Dependencies

依赖现有 Maven parent 继承、reactor 模块关系及当前锁定版本。

## Non-goals

不升级依赖，不重复覆盖 Spring Boot 已管理依赖，不修改 Java 代码、API 或运行时行为。

## Impact

影响 `frame-forward-server` 根 POM 与子模块 POM；验证覆盖依赖管理继承、有效 POM、格式及完整 Maven 质量门禁。
