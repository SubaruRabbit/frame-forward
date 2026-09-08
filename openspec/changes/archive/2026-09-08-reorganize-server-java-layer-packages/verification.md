# 本批验证记录

## 最终迁移审计（2026-09-08）

全部生产模块目录迁移和测试镜像整理已完成；下文 2026-09-07 的「尚未完成」是检查基础设施首批的历史状态，不代表当前状态。本结论针对本地迁移实施与自动化验证，不代替合并评审或发布批准。

1. 全部 202 个生产 Java 文件按模块清点并复核 package/目录；仅 `bootstrap/FrameForwardApplication.java` 保留根包，common 无生产类型。10 个模块的 PackageInventoryTest 在最终 clean 构建实际执行且通过。
2. 全源代码 import 检查未发现 Business→Service/Controller/Repository/Mapper/Gateway/RPC、Manager→Business/Service/Controller/Mapper 或 Repository→上层编排的引用；Service/Business/Manager 无 MyBatis import。gateway/component/model 无上层规则 import。旧根包类型的全限定引用为零。此检查配合已有架构测试，不声称静态文本检查能证明任意动态调用行为。
3. 独立 DTO/record/枚举已进入 model；保留的 MediaService 四个嵌套类型是服务异常，不是跨层 DTO。AI 旧根包实现、适配入口和完成回调桥接已删除。测试按与生产类同名匹配核对，镜像遗漏为零；架构/跨层/集成测试不按单一被测类强制迁移。
4. 最后三批镜像迁移的测试主体（排除 package/import/空白）SHA-256 前后一致，Surefire 在新包实际执行所有指定类；没有删除断言或通过旧 target 类制造通过。
5. `mvn spotless:apply spotless:check` 及 `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 成功：267 个测试，0 失败、0 错误、0 跳过，包含 MySQL Testcontainers 集成/迁移验证。变更行覆盖率 93.03%、分支覆盖率 85.53%、CPD 0.00%，PMD 复杂度门禁通过。日志 `/private/tmp/mirror-portfolio-format.log`、`/private/tmp/mirror-portfolio-verify.log`。
6. `openspec validate --changes --strict`：43 项通过、0 失败。契约及 bootstrap 资源目录无 Git 差异；本次不变更数据库或对外 API 契约。
7. 本次服务端迁移与 OpenSpec 范围 `git diff --check` 通过（不含用户已有 formatter 配置）。全工作区检查仍报告用户已有 `frame-forward-app/package.json:44`、`frame-forward-server/config/eclipse-java-formatter.xml:27` 行尾空白，均保留未修改。

遵守项目宪法、Java 后端工程宪章及 Git 宪章。未提交、归档、合并或发布；人工审批、发布检查和发布回滚演练未执行，因本次仅请求本地迁移。后续若合并/发布，仍须完成宪章规定审批。代码回滚应按各 change 的反向依赖顺序恢复路径及引用后重跑完整门禁，不涉及数据回滚，不得覆盖用户已有修改。

2026-09-07，按用户确认执行。遵守 constitution.md、Java 后端工程宪章与 Git 宪章。基线为 353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef；JDK 21.0.10、Maven 3.9.14。

## 实现范围

- common 新增测试专用 JavaLayerPackages：用 JDK 解析源码，报告 package/目录不一致、根包混放、常见角色错位和包缺失/越界；解析失败或无源码时失败。
- common 测试辅助 jar 仅包含该工具及其嵌套类型，不打包工具的单元测试；auth 仅以 test 范围引用。
- auth 新增迁移前现状测试，确认六个已知类型的错误包位置和目标包；保留原 AuthArchitectureTest。
- migration-inventory.md 覆盖 113 个生产 Java 文件、嵌套数据类型、消费者及分批顺序。

## TDD 与质量证据

1. Red：先新增七个夹具测试，执行 `mvn -pl common spotless:apply test`；在 testCompile 阶段出现六处 JavaLayerPackages 符号缺失，构建按预期失败。
2. Green：加入工具后相同命令通过，7 个测试，无失败、错误或跳过。
3. 接入：`mvn -pl common,auth -am spotless:apply test dependency:tree -Dscope=runtime -Dincludes=com.frameforward:common -B -ntp` 通过；common 7 个、auth 6 个测试通过，auth 运行时依赖过滤结果中无 common。
4. `jar tf common/target/common-0.0.1-SNAPSHOT-tests.jar` 确认仅打包 JavaLayerPackages、Kind、Violation 三个 class 及 Maven 元数据。
5. `mvn spotless:apply spotless:check -B -ntp` 全 reactor 通过；格式化仅影响本批新增测试代码。
6. `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn verify -B -ntp` 全 reactor 通过：125 个测试，0 失败、0 错误、0 跳过，包括 MySQL 容器集成与迁移测试；PMD 圈复杂度检查通过，CPD 重复率 0.00%。
7. 覆盖率门禁明确输出「没有变更的可执行 Java 行，跳过变更代码覆盖率阈值检查」：本批工具位于测试源码，无新增生产可执行行。没有改变覆盖率阈值或报告配置，也不将该跳过结果表述为工具代码达到生产覆盖率阈值。
8. `git diff --check` 通过；未提交、未发布、未修改宪章或现有生产源码。

首次 Maven 沙箱执行因本机缓存写入限制失败；通过授权执行恢复后重跑，以上均为实际成功结果。

## 尚未完成与回滚

本批六项任务完成，但实际目录迁移尚未开始：auth、equipment、media、ai-workflow、shooting、evaluation、generation、course、portfolio、bootstrap 均待后续 change；common 无生产类需要搬迁。现状测试不是分层合规门禁，后续 auth 迁移必须改为零违规断言。

工具不验证调用方向、业务职责或嵌套 DTO 分层；113 个文件的最终迁移与零违规验收尚未执行。发布、合并评审和回滚演练未执行，本批没有发布动作。回滚只撤销两个模块的测试/POM 变更，保留用户其他修改，再运行完整质量门禁。
