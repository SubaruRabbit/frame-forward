## Why

继续已确认的全模块目录迁移，完成媒体 Service 与 DTO 分层。

## What Changes

course 切换目标 MediaService，删除 media 根包兼容 Service，目标 service.MediaService 恢复唯一 Service/Primary 注册。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

frame-forward-server：media、course。

## Dependencies

依赖 migrate-media-service-dtos；用户已授权继续全部迁移。

## Non-goals

不改 HTTP、图片处理、文件清理、数据库、权限和事务语义，不提交发布。
