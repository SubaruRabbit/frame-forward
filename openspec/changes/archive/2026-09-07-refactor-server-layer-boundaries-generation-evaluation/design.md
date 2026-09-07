## Context

SRV-02 的 Mapper 直连证据覆盖 generation、evaluation。

## Goals / Non-Goals

**Goals:** 分离业务规则、编排和持久化。

**Non-Goals:** 不改变任务或结果契约。

## Decisions

- 以架构测试与现有回归测试保护输出。

## Risks / Trade-offs

- [结果持久化回归] → 覆盖完成处理和幂等路径。

## Migration Plan

按模块最小迁移，可回退。
