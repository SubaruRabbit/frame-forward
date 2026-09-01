## Context

参见 SRV-01：`mvn spotless:check` 无插件，且没有覆盖率与静态分析配置。

## Goals / Non-Goals

**Goals:** 让格式、测试、覆盖率与静态分析可本地和 CI 一致执行。

**Non-Goals:** 不修改产品规则或放宽核心系统阈值。

## Decisions

- Spotless 固定 IntelliJ Code Style 并将配置纳入仓库。
- JaCoCo 按本次变更代码测量，静态分析保留可审计报告。
- 格式化与业务迁移分开提交。

## Risks / Trade-offs

- [存量代码阻断] → 以独立格式化提交治理，不扩大排除范围。

## Migration Plan

先配置检查和报告，再在独立提交中修正存量格式；回滚仅移除新增门禁配置。
