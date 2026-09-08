## Why

继续已确认的全模块分层迁移，消除 MediaManager 旧包引用。

## What Changes

切换 evaluation 的 MediaManager 引用，删除 media 根包兼容入口并在目标 Manager 启用唯一 Component Bean。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

frame-forward-server：evaluation、media。

## Dependencies

依赖 migrate-media-manager-consumers；所有消费者切换后才能删除兼容入口。 用户已要求继续全部迁移。

## Non-goals

不改业务、HTTP、数据库、权限和事务；不提交发布。
