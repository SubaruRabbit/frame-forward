## Why

继续已确认的 Java 分层迁移，先收敛 AI 模块内部规则与持久化边界。

## What Changes

五个内部 Graph/Validator 归 business，Manager 归 manager，Mapper 归 mapper；提取 Repository 隔离 MyBatis，同步模块内测试。

## Capabilities

纯分层重构，无业务能力变化。

## Impact

仅 ai-workflow。

## Dependencies

沿用已批准的整体迁移范围，依赖前序 shooting 收尾。

## Non-goals

不改变公共任务入口、DTO、回调、外部消费者、HTTP、状态机、SQL、权限、模型路由和事务；不提交发布。
