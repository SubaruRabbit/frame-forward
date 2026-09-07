## Context

SRV-02 要求协调逻辑与基础设施分离。

## Goals / Non-Goals

**Goals:** 明确 portfolio 与 bootstrap 的职责和依赖方向。

**Non-Goals:** 不改公开 API。

## Decisions

- 用架构测试锁定 Manager/DAO 和 bootstrap 装配边界。

## Risks / Trade-offs

- [删除流程回归] → 覆盖失败、重试与级联清理。

## Migration Plan

按模块迁移并完整回归。
