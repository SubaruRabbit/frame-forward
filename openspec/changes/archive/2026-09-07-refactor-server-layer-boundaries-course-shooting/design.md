## Context

SRV-02 的分层缺口覆盖 course、shooting。

## Goals / Non-Goals

**Goals:** 分离规则、协调和 DAO。

**Non-Goals:** 不改 API 或 AI 输出。

## Decisions

- 先用失败架构测试，后做最小迁移。

## Risks / Trade-offs

- [跨模块调用] → 仅经公开 Service/Port 交互。

## Migration Plan

两模块分别回归、可独立回退。
