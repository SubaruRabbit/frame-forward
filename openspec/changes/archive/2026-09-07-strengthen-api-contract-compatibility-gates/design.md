## Context

参见 CON-01：Redocly 通过但报告 license、4XX response 和未使用 schema 警告。

## Goals / Non-Goals

**Goals:** 使契约校验零未批准警告，并验证向后兼容性。

**Non-Goals:** 不改变 API 行为；语义差异必须停止并单独规格化。

## Decisions

- 以当前已批准主规格和服务端实现作为证据源，不能猜测响应。
- 将 license 和无用组件等文档问题与行为契约问题分离。
- 兼容性检查在 CI 前可本地复现。

## Risks / Trade-offs

- [文档与实现不一致] → 不在本 change 静默修正，另建行为 change。

## Migration Plan

先建立快照/兼容性检查，再修正文档性警告；任何语义变更可单独回滚。
