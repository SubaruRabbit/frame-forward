# 全仓宪章合规基线

> 基线日期：2026-09-01
> 对应 OpenSpec change：`establish-constitution-compliance-baseline`
> 适用规则：`constitution.md`、Java 后端工程宪章、APP 工程开发规范、Git 宪章

## 使用方式

本矩阵记录可复现的当前事实，不以推测代替证据。状态仅可使用：

- `符合`：证据已覆盖该条款的适用范围。
- `不符合`：存在可复现的违反事实，必须映射到整改 change 或正式例外。
- `部分符合`：已有局部实现或检查，但未覆盖全部适用范围；必须映射到整改 change 或正式例外。
- `待确认`：工具或证据不足，不能作为合规结论；必须先补充证据。
- `不适用`：记录不适用原因。

风险按安全、数据正确性、业务正确性、稳定性、可维护性依次评估为“高 / 中 / 低”。涉及敏感信息、凭据、支付/权益最终确认或破坏性操作的红线不得以例外关闭。

## 审计范围与基线命令

| 区域 | 审计主题 | 基线命令或证据类型 |
| --- | --- | --- |
| `frame-forward-server` | Maven、模块依赖、分层、测试、配置、日志和质量门禁 | `mvn -q test`、`mvn spotless:check`、POM/源码/测试的文件与符号证据 |
| `frame-forward-app` | Feature 边界、状态/数据流、网络/存储、安全、无障碍、测试和门禁 | `npm run lint`、`npm run typecheck`、`npm test -- --runInBand`、源码与配置证据 |
| `contracts`、`scripts` | OpenAPI 来源、跨端兼容、校验脚本、提交与发布门禁 | 契约文件、脚本内容、聚合校验命令和 CI 配置证据 |
| 仓库治理 | OpenSpec、Git 纪律、例外、发布与回滚记录 | `openspec validate`、`git status --short`、文档与配置证据 |

## 合规矩阵

| ID | 宪章条款 | 适用区域 | 现状证据 | 状态 | 风险 | 整改边界或例外 | 验证方式 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| GOV-01 | 宪法“规格、实现、测试和发布说明一致”；Java §1 / APP §5 的 SDD | 全仓 | `openspec/specs` 含已同步主规格，现有业务 changes 已归档；本基线已建立矩阵、整改映射和变更计划 | 符合 | 中 | 无 | 对每个整改 change 执行 `openspec validate <name>` 并核对规格、任务和验证记录 |
| GOV-02 | 宪法“不得降低质量门禁”；Java §7；APP §15.2 | 全仓 | `.github/workflows/quality-gates.yml` 在 PR 与 main push 中阻断 APP、服务端、契约、敏感信息与 Conventional Commits；`scripts/check.sh` 聚合三端质量命令 | 符合 | 高 | 无 | 受控 CI 配置与本地 APP/服务端/契约质量命令通过 |
| SRV-01 | Java §2、§5：Java 21、Maven、Spotless/IntelliJ 格式唯一入口 | `frame-forward-server` | Reactor 固定 Java 21、Spotless Eclipse JDT 配置、JaCoCo 与 PMD/CPD；格式化范围覆盖所有模块主/测试 Java 源 | 符合 | 高 | 无 | `mvn -q spotless:check` 与 `QUALITY_BASE_REF=HEAD~1 mvn -q verify` 通过 |
| SRV-02 | Java §3–§4：模块能力划分与 Controller→Service→Business→Manager→DAO 边界 | `frame-forward-server` | auth、equipment 及后续模块均已将 Mapper 访问收束至 Manager；auth/equipment 架构测试验证 Service/Business 不依赖 Mapper，复杂删除和器材规则经 Business 编排 | 符合 | 高 | 无 | 架构测试、模块/API 回归与 `QUALITY_BASE_REF=HEAD~1 mvn -q verify` 通过 |
| SRV-03 | Java §6–§11：TDD、覆盖率、异常、安全、配置、可观测性 | `frame-forward-server` | Testcontainers 隔离测试数据库；dev/prod 数据源仅从环境变量读取；JaCoCo、PMD/CPD 和 CI Gitleaks 已建立 | 符合 | 高 | 无 | `QUALITY_BASE_REF=HEAD~1 mvn -q verify` 与 CI 敏感信息扫描通过 |
| APP-01 | APP §3–§7：Feature 分层、Port 所有权、显式 UI 状态和模型边界 | `frame-forward-app` | Feature Presentation 通过 Application Port/UseCase 调用；网络实现位于 infrastructure，组合测试覆盖跨 Feature 工作流 | 符合 | 高 | 无 | `npm run quality` 通过（29 suites、52 tests），含 Feature、UseCase 与 composition 测试 |
| APP-02 | APP §8–§13：统一网络层、错误恢复、安全存储、隐私、日志与埋点 | `frame-forward-app` | `shared/network/network.ts` 统一超时、取消、HTTP 错误映射；会话凭据使用 Keychain；网络、存储与脱敏日志均有单测 | 符合 | 高 | 无 | `npm run quality` 通过，覆盖网络错误、超时、取消、凭据和脱敏日志 |
| APP-03 | APP §6、§14–§16：无障碍、性能、测试、构建与发布可追溯 | `frame-forward-app` | Prettier、零警告 ESLint、TypeScript、Jest 为阻断命令；共享状态和关键交互含无障碍标签；CI 运行 Android debug、Android 签名 release 与 iOS simulator release 构建 | 符合 | 中 | 无 | `npm run quality` 通过；原生构建门禁见 `.github/workflows/app-native-build.yml` |
| CON-01 | 宪法“API 基于契约”；APP §8、§16：OpenAPI 兼容和回滚 | `contracts`、跨端边界 | Redocly 零警告校验、受版本控制的 OpenAPI 基线和破坏性兼容检查；契约测试覆盖缺失资源、认证响应与 schema 引用 | 符合 | 高 | 无 | `npm run quality` 通过（lint、compatibility、4 项契约测试） |
| OPS-01 | Java §12–§13；APP §16–§17；Git 宪章 | `scripts`、仓库根目录 | 聚合脚本执行 APP/服务端/契约质量命令；受控 CI 执行质量、敏感信息与 Conventional Commits 门禁，并保留服务端报告和原生构建产物 | 符合 | 中 | 无 | `scripts/check.sh` 与 CI workflow 配置 |

## 整改映射规则

每条状态为“`不符合`”或“`部分符合`”的记录，必须在本文件“整改映射”中列出一个 OpenSpec change；确需例外时，只能引用 `docs/constitution/exceptions.md` 中具有负责人、审批人、到期日和清理任务的记录。状态为“`待确认`”不得作为放行依据。

## 整改映射

| 矩阵 ID | 后续 change 或例外 | 当前状态 | 说明 |
| --- | --- | --- | --- |
| GOV-02、OPS-01 | `establish-cross-project-quality-gates` | 已完成 | 已建立受控 CI、提交校验和跨项目质量门禁。 |
| SRV-01 | `establish-server-quality-gates` | 已完成 | 已建立 Spotless Eclipse JDT、覆盖率和静态分析门禁。 |
| SRV-02 | `refactor-server-layer-boundaries-auth-equipment` 至 `refactor-server-layer-boundaries-portfolio-bootstrap` | 已完成 | 按每个 change 最多两个服务端模块完成分层治理，并由架构测试和完整质量门禁验证。 |
| SRV-03 | `isolate-server-tests-and-secure-configuration` | 已完成 | 已消除默认凭据、隔离数据库测试并补足测试运行环境。 |
| APP-01 | `refactor-app-presentation-boundaries-portfolio-reference-image` 至 `refactor-app-presentation-boundaries-photo-review-shooting-session` | 已完成 | 已按每个 change 最多两个 APP Feature 完成边界治理。 |
| APP-02 | `establish-app-network-and-security-boundaries` | 已完成 | 已建立 Network Layer、配置注入和错误映射。 |
| APP-03 | `strengthen-app-quality-accessibility-and-release-gates` | 已完成 | 已收紧 lint、可访问性、构建和发布验证。 |
| CON-01 | `strengthen-api-contract-compatibility-gates` | 已完成 | 已消除契约警告，并建立兼容性契约检查。 |

## 详细证据账本

- 2026-09-01，`frame-forward-server`：`mvn -q -DskipTests=false test` 在 bootstrap 集成测试阶段因本机 MySQL 缺少 `frame-forward` 数据库失败（23 tests，21 errors）；经聚合脚本复跑时又因 Mockito/Byte Buddy 不能附加 JVM 失败。两种结果均表明测试不隔离、不可在无外部前置条件的 CI 中稳定复现。
- 2026-09-01，`frame-forward-server`：`mvn spotless:check` 在已允许写入 Maven 缓存的环境中失败，原因为 POM 未声明 Spotless 插件；根及模块 POM 检索未找到 JaCoCo、Sonar、PMD 或 Checkstyle 配置。
- 2026-09-01，`frame-forward-app`：`npm run typecheck` 通过；经允许访问 Watchman 状态目录后，`npm test -- --runInBand` 通过（11 suites、20 tests）；`npm run lint` 完成但报告 14 个 `no-void` 警告。
- 2026-09-01，`contracts`：安装锁定的开发依赖后，`npm run validate` 通过，Redocly 报告 8 项警告（license、缺少 4XX response 和未使用 schema）。
- 2026-09-01，`scripts/check.sh`：仅调用 APP typecheck、服务端 Maven test 和契约校验；未覆盖 APP lint/Jest、服务端 Spotless/覆盖率/静态分析或敏感信息扫描，且因服务端测试失败未能完成。
- 2026-09-04，`refactor-server-layer-boundaries-auth-equipment`：新增 auth/equipment 架构测试；auth 的 Mapper 访问收束在 `AuthManager`，账号删除复合规则由 `AuthBusiness` 承担；equipment 采用 `UserEquipmentService -> UserEquipmentBusiness -> EquipmentManager`。在非沙箱 JVM 中运行 `QUALITY_BASE_REF=HEAD~1 mvn -q verify` 通过，覆盖 Spotless、全 reactor 测试、JaCoCo、PMD/CPD 与变更覆盖率。
