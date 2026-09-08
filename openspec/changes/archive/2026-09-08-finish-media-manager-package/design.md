## Context

承接 media 基础分层，只修改 evaluation、media，每批不超过两个模块。

## Goals / Non-Goals

切换 evaluation 的 MediaManager 引用，删除 media 根包兼容入口并在目标 Manager 启用唯一 Component Bean。 不改变可观察行为，不将 Manager 收尾视为全部 media 或整体目标完成。

## Decisions

目标类型 com.frameforward.media.manager.MediaManager 已实现。生产构造器与测试 mock 同步引用目标类型，所有权、流程与查询语义保持。依赖 migrate-media-manager-consumers；所有消费者切换后才能删除兼容入口。

## Risks / Trade-offs

包引用、Spring 注入和 mock 类型可能回归；新增类型断言先 Red，再切换实现，保留原业务测试。

## Migration Plan

先失败测试，再改引用，最后完整 Spotless apply/check 及 QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify。回滚逆序恢复本批引用、注解及入口后重跑门禁，保留前序和用户修改。后续继续 media 的 Service、Entity、DTO、投影和端口，再推进其余模块。
