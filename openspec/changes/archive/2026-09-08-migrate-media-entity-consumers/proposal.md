## Why

继续已确认的全模块分层迁移，完成媒体实体目录规范化。

## What Changes

生产与测试统一规范 MediaEntity，并使用 findOwnedEntity 查询，保持所有权、快照和生成行为。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

frame-forward-server：shooting、generation。

## Dependencies

依赖 migrate-media-entity-foundation，用户已授权继续全部迁移。

## Non-goals

不改表结构、SQL、权限、事务、媒体数据和 HTTP 行为，不提交发布。
