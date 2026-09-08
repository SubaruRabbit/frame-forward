## Why

继续已确认的全模块目录迁移，完成媒体 Service 与 DTO 分层。

## What Changes

MediaService 移至 service，MediaResponse、UploadProgress 移至 model.dto；媒体内调用与 generation 的生产和测试引用同步。原 Service 暂留唯一 Bean 的无逻辑兼容入口，供 course 使用。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

frame-forward-server：media、generation。

## Dependencies

依赖已完成 MediaManager 收尾；用户已授权继续全部迁移。

## Non-goals

不改 HTTP、图片处理、文件清理、数据库、权限和事务语义，不提交发布。
