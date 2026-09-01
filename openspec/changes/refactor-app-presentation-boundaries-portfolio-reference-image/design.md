## Context

APP-01 显示两页面直接依赖基础设施。

## Goals / Non-Goals

**Goals:** 页面只绑定状态和事件，UseCase 经 Port 调用基础设施。

**Non-Goals:** 不新增功能。

## Decisions

- 先以组件/UseCase 测试锁定现有状态，再替换依赖。

## Risks / Trade-offs

- [轮询回归] → 覆盖取消、失败和恢复。

## Migration Plan

逐 Feature 迁移，可独立回退。
