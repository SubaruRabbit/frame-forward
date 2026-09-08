## Why

继续已确认的全模块分层迁移，完成媒体实体目录规范化。

## What Changes

evaluation 切换规范实体和查询；media Mapper/Repository/Service/测试统一规范实体，删除根包实体和旧查询入口，启用全 media 零目录违规断言。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

frame-forward-server：evaluation、media。

## Dependencies

依赖 migrate-media-entity-consumers，用户已授权继续全部迁移。

## Non-goals

不改表结构、SQL、权限、事务、媒体数据和 HTTP 行为，不提交发布。
