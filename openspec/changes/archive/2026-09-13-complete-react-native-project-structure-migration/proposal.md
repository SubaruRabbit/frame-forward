## Why

上一迁移批次仍以限期例外保留根目录 `features/**`，因此项目尚未满足 RN 目录规范且禁止合并发布。必须在例外到期前完成迁移并撤销临时门禁。

## What Changes

- 在五个前置批次完成后，将最后的 `shooting-session` 模块迁入 `src/features/shooting-session`，保持行为和测试不变。
- 为 `shooting-session` 建立公开入口，修正 app 装配导入并消除深路径访问。
- 删除旧目录和限期例外配置，更新架构检查、文档及发布门禁。
- 运行 Android/iOS 类型、测试、Metro bundle 与原生构建验证。

## Capabilities

本变更是无产品行为变化的纯重构，已设置 `skip_specs: true`。

## Dependencies

- 已完成的 `refactor-react-native-project-structure` 基础变更。
- `migrate-auth-equipment-features-to-src`。
- `migrate-learning-photo-import-features-to-src`。
- `migrate-photo-review-portfolio-features-to-src`。
- `migrate-reference-image-scene-analysis-features-to-src`。
- `migrate-settings-shooting-plan-features-to-src`。
- 当前受控例外 `architecture/legacy-migration.json`。

## Non-goals

- 不修改用户流程、业务规则、API、持久化格式或原生依赖。
- 不进行功能重写、视觉改版或无关依赖升级。

## Impact

影响 `frame-forward-app/features/shooting-session`、`src/features/shooting-session`、`src/app/workflowComposition.ts`、架构检查配置和迁移文档；服务端及 OpenAPI 契约不变。
