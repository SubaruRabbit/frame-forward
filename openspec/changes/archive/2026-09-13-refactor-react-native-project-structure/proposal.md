## Why

`frame-forward-app` 仍使用根级 `app/features/shared` 与旧分层，缺少 RN 专项宪章要求的 `src/` 归属、公开入口和依赖门禁，继续开发会扩大结构债务。

## What Changes

- 建立 `src/app`、`services`、`components`、`contracts`、`config`、`theme`、`i18n` 基础结构。
- 迁移应用装配和共享基础能力，并统一别名、Metro、Babel、Jest、ESLint 架构检查。
- 保持现有路由、会话和业务行为不变。

## Capabilities

纯工程重构，不新增或修改产品能力；本 change 使用 `skip_specs`。

## Dependencies

依赖现有 app-shell、认证、网络与安全存储公开行为。

## Non-goals

不迁移 feature 内部目录，不修改 API、业务规则、原生能力或产品 UI。

## Impact

影响 `frame-forward-app` 装配、共享基础设施、工程配置及相关测试；不影响服务端与 contracts。
