## Context

SRV-02 证据显示两模块 Service 直接调用 Mapper。

## Goals / Non-Goals

**Goals:** 建立 Controller→Service→Business→Manager→Mapper 边界和架构测试。

**Non-Goals:** 不改变对外行为。

## Decisions

- 先以失败架构测试锁定依赖方向，再做最小迁移。

## Risks / Trade-offs

- [事务边界漂移] → 保持 Service 用例事务并以回归测试验证。

## Migration Plan

按模块分步迁移，可独立回退。
