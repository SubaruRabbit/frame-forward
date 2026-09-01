# FrameForward OpenSpec Change Plan

本计划以批准版PRD为需求基线。每个change只负责一个主能力；实现时一次只apply一个change，完成验证并archive后再进入依赖它的change。

## 执行顺序

| 阶段 | Change | 单一职责 | 任务数 | 直接依赖 |
|---:|---|---|---:|---|
| 0 | `bootstrap-workspace` | 初始化App、服务端和契约工程 | 4 | 无 |
| 1 | `build-app-shell` | Android导航、门禁和通用页面状态 | 4 | bootstrap |
| 2 | `add-password-authentication` | 密码账号与会话 | 7 | bootstrap, app-shell |
| 3A | `add-equipment-catalog` | 只读器材事实目录 | 4 | auth |
| 3B | `ingest-jpeg-media` | JPEG、EXIF与私有存储 | 5 | bootstrap, auth |
| 3C | `run-ai-tasks` | AI异步任务运行时 | 7 | bootstrap, auth |
| 4A | `manage-user-equipment` | 用户器材与主力相机 | 6 | catalog, auth |
| 4B | `deliver-ai-courses` | AI课程、版本与进度 | 7 | AI runtime, catalog, auth |
| 5 | `analyze-shooting-scene` | 现场环境结构化分析 | 5 | media, AI runtime, user equipment |
| 6A | `generate-shooting-plans` | 器材约束拍摄方案 | 5 | scene analysis, user equipment |
| 6B | `evaluate-photos` | 单张照片评分与诊断 | 6 | media, AI runtime, user equipment |
| 7A | `generate-reference-images` | 已选方案的参考图 | 5 | shooting plans, AI runtime, media |
| 7B | `compare-retakes` | 原作与重拍进步对比 | 5 | evaluation, shooting plans |
| 8 | `browse-portfolio` | 私有作品集浏览与筛选 | 5 | evaluation, retake comparison, auth |
| 9 | `delete-work-data` | 单作品及派生数据删除 | 5 | portfolio, media, evaluation |
| 10 | `delete-account-data` | 账号注销与全量清理编排 | 7 | auth, work deletion, courses |

同一阶段带不同字母的change，在其直接依赖完成后可以独立推进；这不表示必须并行执行。

## 粒度守则

- proposal只声明一个new capability；纯工程初始化显式 `skip_specs`。
- 每个change最多5条requirements，且每条至少一个scenario。
- 每个change保持3～7个tasks，每个task只指向一个项目或服务端模块并包含验证方式。
- apply过程中若任务超过7项、需要第二个主能力或跨越两个以上业务模块，立即停止并拆出新change。
- 不在当前changes中加入部署、商业化、短信验证、密码恢复、相机直连或课程CMS。

## 宪章合规整改计划

本计划由 `establish-constitution-compliance-baseline` 的证据矩阵驱动。先处理凭据保护和测试隔离等红线，再建立可阻断的质量门禁，最后按不超过两个服务端模块或两个 APP Feature 的边界重构。每一项均须单独 propose，保持 3–7 个任务；任何 API、数据或业务行为改变必须同时附带对应 delta spec、兼容策略与回滚方案。

| 顺序 | Change | 单一职责与受影响范围 | 任务数 | 直接依赖 | 验收命令或证据 |
|---:|---|---|---:|---|---|
| C0 | `isolate-server-tests-and-secure-configuration` | 移除服务端默认凭据，并使测试数据库与外部环境隔离；仅 `bootstrap`、`auth` 与测试配置 | 5–7 | 基线 | `mvn -q test` 在无本机 MySQL/凭据前置条件下通过；敏感信息扫描无默认密码 |
| C1 | `establish-server-quality-gates` | 为 Maven reactor 建立 Spotless（IntelliJ 样式）、JaCoCo 和静态分析入口；仅根 POM 与 `bootstrap` | 4–6 | C0 | `mvn spotless:check`、`mvn test`、覆盖率/静态分析命令均可执行且阻断失败 |
| C2 | `establish-app-network-and-security-boundaries` | 建立 APP Network Layer、环境配置、错误映射与安全存储 Port；仅 `app`、`shared` | 5–7 | 基线 | 网络/存储单测，敏感信息扫描，取消/超时/认证失败场景通过 |
| C3 | `strengthen-api-contract-compatibility-gates` | 消除 OpenAPI 警告并建立兼容性/契约校验；仅 `contracts` | 4–6 | C0、C2 | `npm run validate` 零未批准警告；契约兼容测试通过 |
| C4 | `refactor-server-layer-boundaries-auth-equipment` | 将 auth、equipment 的业务规则和数据编排迁移到 Business/Manager 边界 | 5–7 | C0、C1 | 架构依赖测试、模块测试、覆盖率门禁通过 |
| C5 | `refactor-server-layer-boundaries-media-ai-workflow` | 将 media、ai-workflow 迁移到规定分层边界 | 5–7 | C0、C1 | 架构依赖测试、模块测试、覆盖率门禁通过 |
| C6 | `refactor-server-layer-boundaries-course-shooting` | 将 course、shooting 迁移到规定分层边界 | 5–7 | C0、C1、C4、C5 | 架构依赖测试、模块测试、覆盖率门禁通过 |
| C7 | `refactor-server-layer-boundaries-generation-evaluation` | 将 generation、evaluation 迁移到规定分层边界 | 5–7 | C0、C1、C5、C6 | 架构依赖测试、模块测试、覆盖率门禁通过 |
| C8 | `refactor-server-layer-boundaries-portfolio-bootstrap` | 将 portfolio、bootstrap 的跨模块编排和基础设施边界迁移到规定层次 | 4–6 | C0、C1、C7 | 架构依赖测试、完整服务端测试和覆盖率门禁通过 |
| C9 | `refactor-app-presentation-boundaries-portfolio-reference-image` | 从 portfolio、reference-image Presentation 移除 `fetch` 与存储依赖 | 5–7 | C2、C3 | Feature 边界测试、组件/UseCase 测试、typecheck 通过 |
| C10 | `refactor-app-presentation-boundaries-scene-analysis-shooting-plan` | 从 scene-analysis、shooting-plan Presentation 移除基础设施依赖 | 5–7 | C2、C3 | Feature 边界测试、组件/UseCase 测试、typecheck 通过 |
| C11 | `refactor-app-presentation-boundaries-equipment-learning` | 从 equipment、learning Presentation 移除基础设施依赖 | 5–7 | C2、C3 | Feature 边界测试、组件/UseCase 测试、typecheck 通过 |
| C12 | `refactor-app-presentation-boundaries-auth-photo-import` | 从 auth、photo-import Presentation 移除基础设施依赖 | 5–7 | C2、C3 | Feature 边界测试、组件/UseCase 测试、typecheck 通过 |
| C13 | `refactor-app-presentation-boundaries-photo-review-shooting-session` | 整理 photo-review、shooting-session 的状态与交互边界 | 4–6 | C2、C3 | Feature 边界测试、组件/UseCase 测试、typecheck 通过 |
| C14 | `strengthen-app-quality-accessibility-and-release-gates` | 消除 APP lint 警告，建立无障碍、构建和发布验证；仅质量配置与共享 UI | 5–7 | C2、C9–C13 | lint 零警告、typecheck、Jest、Android/iOS 构建与无障碍检查通过 |
| C15 | `establish-cross-project-quality-gates` | 在受控 CI 中编排服务端、APP、契约、提交和发布门禁；仅根目录、`scripts`、CI 配置 | 5–7 | C1、C3、C8、C14 | CI 对格式、lint、测试、覆盖率、契约、敏感信息和 Conventional Commits 失败均阻断 |

执行时每完成一个 change，必须更新 `docs/constitution/compliance-baseline.md` 的状态、证据和整改映射；不得以扩大忽略范围、降低阈值或关闭检查替代整改。
