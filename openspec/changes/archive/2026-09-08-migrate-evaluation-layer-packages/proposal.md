## Why

继续已确认的分层迁移，消除 evaluation 根包及 Service/Manager 直连 Mapper。

## What Changes

按职责分包，提取请求、作品查询 DTO 和异常；评测、会话和清理经 Manager/Repository 持久化，同步 portfolio 查询及清理端口引用。

## Capabilities

无业务能力变化，纯重构。

## Impact

仅 evaluation、portfolio 两个模块。

## Dependencies

依赖已完成的 media/portfolio 迁移，沿用用户确认的剩余模块迁移范围。

## Non-goals

不改评分、重拍比较、查询条件、JSON、权限、事务、表结构或删除顺序，不提交发布。
