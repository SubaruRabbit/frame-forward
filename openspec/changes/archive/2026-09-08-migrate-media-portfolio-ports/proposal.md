## Why

继续已确认的媒体分层，清除作品查询和清理端口的根包堆叠。

## What Changes

- WorkMediaCleanup 迁入 gateway；PortfolioMediaQuery 迁入 service，投影 record 迁入 model.dto，portfolio 同步引用。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

仅 frame-forward-server 的 media、portfolio。

## Dependencies

依赖 media Manager/Service 已完成的迁移；用户已授权继续剩余模块。

## Non-goals

不改作品筛选、删除、授权、EXIF 或 HTTP 行为，不提交发布。
