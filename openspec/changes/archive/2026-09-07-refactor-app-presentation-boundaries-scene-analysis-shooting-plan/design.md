## Context

APP-01 要求 Presentation 不直接依赖网络。

## Goals / Non-Goals

**Goals:** 显式建模加载、成功、失败、取消和恢复状态。

**Non-Goals:** 不变更结果契约。

## Decisions

- UseCase 管理轮询与取消，页面只渲染状态。

## Risks / Trade-offs

- [无限轮询] → 设置超时、取消和重试上限。

## Migration Plan

各 Feature 独立回归和回退。
