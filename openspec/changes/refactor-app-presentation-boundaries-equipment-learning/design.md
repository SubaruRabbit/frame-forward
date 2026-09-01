## Context

APP-01 要求 Feature 边界和显式状态。
## Goals / Non-Goals

**Goals:** 抽取 UseCase/Port 并保护现有状态。
**Non-Goals:** 不新增产品能力。
## Decisions

- 先写组件/UseCase 测试，再替换页面依赖。
## Risks / Trade-offs

- [器材状态回归] → 覆盖空、失败、重复操作。
## Migration Plan

逐 Feature 回归和回退。
