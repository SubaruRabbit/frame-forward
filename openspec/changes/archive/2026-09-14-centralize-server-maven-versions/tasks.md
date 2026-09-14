## 1. 依赖管理实现

- [x] 1.1 在 `frame-forward-server/pom.xml` 添加 `dependencyManagement`，管理实际被消费的内部模块、`common` tests test-jar、MyBatis Plus 和 metadata-extractor，并通过 XML 检查确认所有管理项引用根属性或 `${project.version}`。
- [x] 1.2 删除 `frame-forward-server` 子模块中所有受管依赖的 `<version>`，保留 type、classifier 和 scope，并通过全 reactor POM 扫描确认插件版本仍直接引用根属性且受管依赖无重复版本声明。

## 2. Maven 模型验证

- [x] 2.1 在 `frame-forward-server` 执行 `mvn help:effective-pom` 和 `mvn dependency:tree`，确认全部模块模型可解析、`common` test-jar 正确匹配且内部模块、MyBatis Plus、metadata-extractor 与 Spring Boot BOM 依赖版本均未改变。

## 3. 格式与质量验证

- [x] 3.1 在 `frame-forward-server` 执行 `mvn spotless:apply` 后执行 `mvn spotless:check`，确认全部修改的 POM 符合仓库格式规则且未产生范围外改动。
- [x] 3.2 在 `frame-forward-server` 执行 `QUALITY_BASE_REF=HEAD mvn -q -DskipTests=false verify`，确认完整 reactor 构建、测试、JaCoCo、PMD/CPD 和质量报告校验全部通过。
- [x] 3.3 执行 `openspec validate centralize-server-maven-versions` 并复查 Git diff，确认 OpenSpec 产物有效、仅包含计划内 POM 与 change 文件且没有依赖版本升级。
