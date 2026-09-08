## Context

继续已确认的分层迁移；本批只涉及 bootstrap，无其他模块 Java 消费者，独立提前整理不改变业务模块迁移依赖顺序。

## Goals / Non-Goals

配置在 config、清理实现在 repository，只有 FrameForwardApplication 留根包。不改变 SQL、删除顺序、账号隔离或注册方式。

## Decisions

保留 Configuration 显式 Bean 装配，不将清理类改成扫描组件；数据库类及构造器仅为跨包装配开放 public。复用测试检查器，只允许启动文件的一条 ROOT_PACKAGE 结果，不整体排除根目录。测试同步职责目录和源码引用。

## Risks / Trade-offs

删除属于高风险领域，必须证明原 SQL 的账户参数、顺序与失败传播完全不变；现有删除集成测试必须通过。

## Migration Plan

先运行目录失败测试，再迁移并验证清理顺序/失败传播、装配和完整 reactor。执行 Spotless apply/check 及基线 353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify。回滚只恢复本批路径、可见性和测试引用，不触碰数据及前序变更，再跑完整门禁。
