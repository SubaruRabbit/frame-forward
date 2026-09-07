## Context

SRV-02 发现 Service/运行时直接编排 Mapper。

## Goals / Non-Goals

**Goals:** 明确规则、流程和持久化职责。

**Non-Goals:** 不改变任务状态机或媒体访问语义。

## Decisions

- 以架构测试先锁定依赖方向，逐模块迁移。

## Risks / Trade-offs

- [异步恢复回归] → 覆盖重试和恢复测试。

## Migration Plan

每模块独立迁移和回归。
