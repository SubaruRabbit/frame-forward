## Context

APP-01 要求页面使用显式、可恢复状态。
## Goals / Non-Goals
**Goals:** 用 UseCase 和 UI state 表达加载、成功、失败和恢复。
**Non-Goals:** 不改后端契约。
## Decisions

- 页面只绑定 state 和事件。
## Risks / Trade-offs

- [状态遗漏] → 覆盖异常和重复操作测试。
## Migration Plan

逐 Feature 回归和回退。
