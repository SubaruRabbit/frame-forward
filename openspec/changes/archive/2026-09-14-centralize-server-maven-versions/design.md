## Context

参见 [proposal.md](./proposal.md) 的动机。`frame-forward-server` 是根 POM 加 11 个子模块组成的 Maven reactor。第一轮整理已将显式版本值集中到根 `properties`，但子模块仍逐项引用版本属性，内部模块版本声明也重复存在；Spring Boot 管理的依赖则按 parent BOM 省略版本。

## Goals / Non-Goals

**Goals:**

- 在根 POM 使用 `dependencyManagement` 管理实际被其他模块依赖的内部构件，以及当前显式定版的 MyBatis Plus 和 metadata-extractor。
- 让子模块依赖只声明坐标、类型、classifier 和 scope，由继承的管理项提供版本。
- 保留根 `properties` 作为所有自管依赖、构建插件、插件依赖及格式器的唯一版本值来源，并保持有效 POM 的解析版本不变。
- 覆盖项目：`frame-forward-server`；涉及模块：根、`ai-workflow`、`auth`、`bootstrap`、`course`、`equipment`、`evaluation`、`generation`、`media`、`portfolio`、`shooting`。

**Non-Goals:**

- 不集中 Spring Boot parent 自身版本；Maven 在读取当前 POM 的 `properties` 前解析 parent，不能可靠引用同一 POM 内属性。
- 不在本项目的 `dependencyManagement` 重复声明 Spring Boot parent 已管理且本次没有显式定版的普通依赖。
- 不用 `dependencyManagement` 管理 Maven 插件、插件内部依赖或 Spotless 的 Eclipse formatter；这些不属于项目依赖解析域，继续直接引用根属性。
- 不调整项目坐标，不升级任何依赖，不改变插件执行配置。

## Decisions

1. 根 `dependencyManagement` 管理所有实际被消费的 `com.frameforward` 普通模块构件，版本统一为 `${project.version}`。相比子模块逐项填写 `${project.version}`，管理项使依赖声明只表达依赖关系，并为后续版本调整提供单一入口。
2. `common` 的普通 JAR 与带 `classifier=tests`、`type=test-jar` 的测试构件分别声明管理项。Maven 依赖管理按 groupId、artifactId、type、classifier 匹配，单一普通 JAR 管理项不能可靠覆盖 test-jar 依赖。
3. MyBatis Plus 与 metadata-extractor 在根管理项中引用 `mybatis-plus.version` 和 `metadata-extractor.version`，各业务模块删除 `<version>`。Spring Boot 已管理的 starter、Testcontainers、Flyway 普通依赖和运行时 MySQL 不重复声明，避免更近的管理项覆盖 BOM 解析结果。
4. 插件版本仍在 `build/plugins` 直接引用根属性；Flyway 插件内的 `flyway-mysql` 和 `mysql-connector-j` 也保留显式属性引用。项目 `dependencyManagement` 不用于 Maven 插件依赖域，强行迁移可能造成版本缺失或改变普通 MySQL 依赖版本。
5. 验证以静态扫描、`help:effective-pom`、`dependency:tree` 和完整构建为准：子模块的受管依赖不得残留 `<version>`，有效模型与依赖树中的版本必须保持原值，再运行 Spotless 和带基线的完整 `mvn verify`。

## Risks / Trade-offs

- [管理项的 type/classifier 不匹配会使 test-jar 缺失版本] → 为 `common` 测试构件建立独立管理项，并解析全部模块有效 POM。
- [更近的管理项可能覆盖 Spring Boot BOM] → 只管理本项目原先显式定版的第三方依赖，不纳入 BOM 已管理的其他依赖。
- [删除子模块版本后可能掩盖错误继承] → 使用 `dependency:tree` 和完整 reactor 构建验证每个消费模块。
- [POM 格式化改变较多行] → 只修改依赖管理和版本元素，使用项目既有 Spotless POM 规则校验，不做无关重排。

## Migration Plan

1. 在根 POM 添加内部构件、MyBatis Plus 与 metadata-extractor 的 `dependencyManagement` 管理项。
2. 删除子模块对应依赖的 `<version>`，保留 type、classifier 和 scope。
3. 验证有效 POM、依赖树、POM 格式和完整质量门禁。
4. 如需回滚，删除新增管理项并恢复子模块原有版本引用；不涉及数据或运行时迁移。
