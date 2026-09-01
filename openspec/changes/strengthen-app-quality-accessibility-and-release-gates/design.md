## Context

APP-03 记录了 14 个 lint 警告和缺失的发布验证。
## Goals / Non-Goals
**Goals:** 建立可复现的质量、无障碍和构建门禁。
**Non-Goals:** 不降低警告等级。
## Decisions

- 修正根因，不扩大忽略规则；可访问名称和缩放作为共享 UI 检查。
## Risks / Trade-offs

- [原生构建差异] → 分 Android/iOS 记录环境和产物。
## Migration Plan

先清 lint 和共享组件，再接入构建检查。
