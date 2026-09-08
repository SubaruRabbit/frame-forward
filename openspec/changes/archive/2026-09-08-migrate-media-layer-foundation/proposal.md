## Why

继续已确认的剩余模块分层迁移，先建立 media 的控制器、协调和持久化目录。

## What Changes

- media Controller、错误映射、Mapper 迁入职责包；新增 Repository 隔离查询，Manager 迁入目标目录并保留临时兼容入口。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

仅 frame-forward-server/media。

## Dependencies

沿用已确认的全模块迁移清单及 auth 迁移；用户要求继续全部剩余模块。

## Non-goals

不改变 HTTP、文件处理、数据库或权限行为；本批不切换跨模块消费者，不提交发布。
