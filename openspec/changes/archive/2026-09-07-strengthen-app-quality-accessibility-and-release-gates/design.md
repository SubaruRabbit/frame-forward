## Context

APP-03 记录了 14 个 lint 警告和缺失的发布验证。
## Goals / Non-Goals
**Goals:** 建立可复现的质量、无障碍和构建门禁。
**Non-Goals:** 不降低警告等级。
## Decisions

- 修正根因，不扩大忽略规则；可访问名称和缩放作为共享 UI 检查。
- APP 代码格式化采用受版本控制的 EditorConfig、Prettier 和 ESLint：EditorConfig 管理文本基线，Prettier 仅格式化 JS/TS 与结构化文本，ESLint 负责 React Native 静态质量并以 `eslint-config-prettier` 消除格式规则冲突；不使用 `eslint-plugin-prettier`。
- 新增 `format`、`format:check`、零警告 `lint`、`lint:fix` 与 `quality` 命令；`quality` 按格式检查、lint、typecheck、Jest 顺序运行。受控 CI 在 Node 22 中使用 `npm ci` 后执行该命令。
- 引入配置不触发全量格式化；Prettier 与 ESLint 仅排除依赖、构建、覆盖率和原生/生成输入，不得排除业务代码或降低告警等级。
## Risks / Trade-offs

- [原生构建差异] → 分 Android/iOS 记录环境和产物。
## Migration Plan

先建立格式化和零警告 lint 基线，再清理共享组件并接入构建检查；历史源码格式化必须作为独立变更。
