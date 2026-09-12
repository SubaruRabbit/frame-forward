## Context

See [proposal.md](./proposal.md). 当前 `frame-forward-app` 的业务源码位于根级 `app/`、`features/`、`shared/`；跨模块通过相对深路径访问，TypeScript、Babel、Metro、Jest 未配置统一别名，ESLint 也只提供通用语法检查。首批迁移必须保持 app-shell、会话、路由和网络行为不变，并遵守单个 change 不跨越两个以上 feature 的限制。

## Goals / Non-Goals

**Goals:**

- 建立能够承载后续分批迁移的 `src/` 目录、公开入口和统一解析规则。
- 将应用装配、会话、路由、网络、存储、日志和无业务 UI 放入 RN 专项规范规定的区域。
- 提供覆盖生产代码、测试代码、平台文件、公开入口和受限 SDK 的阻断式架构检查。
- 保持现有导出契约和可见行为，并让后续 feature 每批最多迁移两个模块。

**Non-Goals:**

- 本 change 不重写 feature 内部的 Application/Domain/Infrastructure 代码，也不改变其业务接口。
- 本 change 不引入查询缓存、全局状态库、导航库或新的产品能力。
- 本 change 不修改原生工程、服务端、OpenAPI 或用户数据。

## Decisions

### 1. 先迁移装配与通用边界，再迁移 feature

目标归属如下：

| 当前路径 | 目标路径 | 责任 |
| --- | --- | --- |
| `app/AppShell.tsx`、`app/workflowComposition.ts` | `src/app/` | 根装配与 feature 能力连接 |
| `app/routes.ts` | `src/contracts/navigation.ts` 与 `src/app/navigation/` | 可共享路由契约与 app 路由状态 |
| `app/session.ts` | `src/domains/session/` | 共享会话规则与公开入口 |
| `app/environment.ts` | `src/config/environment.ts` | 环境读取与校验 |
| `shared/network/` | `src/services/api/` | HTTP 传输与错误归一化 |
| `shared/storage/`、`shared/security/` | `src/services/storage/` | AsyncStorage 与 Keychain 适配 |
| `shared/logging/` | `src/services/logging/` | 日志适配与脱敏 |
| `shared/components/` | `src/components/` | 无业务语义组件及组件级入口 |

`App.tsx` 只调用 `@app` 的根装配入口；模块内部使用相对路径，跨模块只使用目标模块公开入口。未迁移 feature 暂由精确路径清单桥接，后续按模块删除对应项；清单不是可合并豁免，完整目标验收前必须清零。

备选方案是一次性移动所有 feature。该方案会超过 OpenSpec 的 feature 粒度限制、扩大评审面并使失败难以定位，因此不采用。

### 2. 统一 TypeScript、Babel、Metro 与 Jest 的别名语义

TypeScript `paths` 按专项规范定义 `@app/*`、`@features/*`、`@domains/*`、`@components/*`、`@hooks/*`、`@utils/*`、`@theme/*`、`@services/*`、`@contracts/*`、`@config/*`、`@i18n`、`@assets/*`。Babel 使用 `babel-plugin-module-resolver` 执行运行时改写；Metro 保留 RN 默认配置，Jest 继续走同一 Babel 转换链，只在实际解析差异出现时增加精确 mapper。

备选方案是继续使用相对路径，但它无法稳定表达跨模块公开入口，也无法满足规范，因此不采用。新增开发依赖仅限 module resolver，并通过锁文件固定。

### 3. ESLint 与独立架构检查各司其职

ESLint 保持零警告代码质量检查；新增基于已安装 TypeScript Compiler API 的 `scripts/check-architecture.mjs`，解析静态 import/export、`require` 与静态动态导入，按 iOS、Android、native 和通用回退分别构图。检查器执行完整区域矩阵、feature/domain 模块边界、公开入口、`registration.ts` 权限、生产/测试访问范围、循环依赖以及受限 SDK、全局 `fetch`、`XMLHttpRequest` 调用位置。

`npm run architecture:check` 单独可运行，并加入 `quality` 阻断链。配置必须带允许与拒绝 fixture 测试，不能用 ignorePatterns 排除业务或测试源码。

备选方案是只使用 ESLint `no-restricted-imports`。该规则不能可靠覆盖解析后的深路径、平台图、转导出与循环，因此只作为可读提示，不作为唯一证据。

### 4. 公开入口兼容而非双重实现

每个迁移模块只保留一个实现和一个规范公开入口。旧路径只在迁移中的内部调用被同一 change 机械更新，不创建长期 re-export 兼容层，避免出现两个可竞争的入口。测试随所属文件迁移；根集成测试只访问模块公开入口或明确的 app 测试装配入口。

## Risks / Trade-offs

- [分批迁移期间同时存在根级 feature 与 `src/`] -> 使用逐文件迁移清单和边界 fixture；最终验收要求旧业务目录与清单同时清零。
- [别名在类型检查、打包和测试中的解析不一致] -> 同一映射生成或逐项对齐配置，并对 Android、iOS、Jest 分别执行解析验证。
- [机械移动导致测试 mock 或相对资源路径失效] -> 每批移动同步更新测试与 mock，先执行架构 fixture，再执行 `npm run quality`。
- [自建检查器漏掉动态语法] -> 静态目标全部解析；无法静态确定的生产导入直接失败，只有带负责人、范围和清理条件的精确记录可进入评审。
- [新增 Babel 插件增加维护成本] -> 仅引入一个开发期解析依赖，锁定版本，并由 TS/Metro/Jest 解析测试覆盖。

## Approved Temporary Exception

| Item | Record |
| --- | --- |
| Violated clauses | RN 专项 §1.2、§2.1、§3.3、§7.4：既有根级 `features/` 及其旧内部目录、深路径入口尚不能在首批 change 中全部迁入 `src/features/` 和公开入口。 |
| Business reason | OpenSpec 规定单个 change 最多影响两个 APP feature；一次迁移全部模块会失去可评审、可验证和可回退的批次边界。 |
| Scope | 仅限批准时已经存在的根级 `frame-forward-app/features/**` 文件及其既有导入边；不得新增 legacy 文件、扩大路径模式或借此豁免已迁入 `src/` 的代码。 |
| Risk | 中等：迁移期间存在双结构，可能产生解析差异、深路径继续扩散或架构检查误判。 |
| Compensating controls | 架构检查必须扫描全部生产与测试源码；精确清单逐文件记录 legacy 偏离并在每批删除对应项。新增或修改后的非迁移 legacy 路径直接失败。安全、凭据、会话、网络直连、受限 SDK、循环依赖、测试泄漏和业务正确性规则始终阻断，不在例外范围。每批继续执行 `npm run quality` 与适用平台验证。 |
| Merge and release | 例外存续期间不得合并或发布；只有 legacy 清单与旧业务目录清零、完整门禁通过并经技术负责人验收后解除。 |
| Owner | 当前变更实施代理负责维护清单和执行迁移；项目技术负责人负责最终验收。 |
| Approver and evidence | 项目技术负责人（用户）于 2026-09-12 在本任务会话中明确批准该限期迁移例外。 |
| Effective period | 2026-09-12 至 2026-10-12，不自动续期；到期未清理则继续停止合并和发布。 |
| Cleanup task | 后续按 feature 批次迁移并逐项删除清单；最终 `complete-react-native-project-structure-migration` change 删除 legacy 清单与根级业务目录，执行完整架构检查、质量命令及 Android/iOS 构建。 |

## Migration Plan

1. 本 change 建立目标目录、公开入口、解析配置和架构检查器，迁移 app、session 与通用基础能力。
2. 后续 change 按最多两个 feature 一批迁移 `auth/photo-import`、`portfolio/reference-image`、`scene-analysis/shooting-plan`、`equipment/learning`、`photo-review/shooting-session`，同步删除迁移清单项。
3. 最终 change 清理旧目录和全部临时清单，执行格式、零警告 lint、架构检查、typecheck、Jest、Android/iOS 打包解析与受影响平台构建。
4. 单批失败时回退该批路径和配置映射；不回滚已经验证且没有旧入口依赖的前置批次。数据与 API 未改变，无需数据回滚。
