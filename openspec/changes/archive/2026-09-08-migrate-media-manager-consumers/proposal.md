## Why

继续已确认的全模块分层迁移，消除 MediaManager 旧包引用。

## What Changes

先切换 shooting 与 generation 的 MediaManager 生产与测试引用，保持媒体实现不变。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

frame-forward-server：shooting、generation。

## Dependencies

依赖 migrate-media-layer-foundation；本批之后再由 evaluation 与 media 批次删除旧入口。 用户已要求继续全部迁移。

## Non-goals

不改业务、HTTP、数据库、权限和事务；不提交发布。
