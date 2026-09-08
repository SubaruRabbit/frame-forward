## Why

建立 auth 目标分层实现，封装持久化并保留迁移期兼容入口，落实已确认的服务端目录迁移计划。

## What Changes

- 只修改 auth、bootstrap 模块；保持 HTTP、数据库与权限行为。

## Capabilities

### New Capabilities

无，纯工程重构，skip_specs。

### Modified Capabilities

无。

## Impact

frame-forward-server：auth、bootstrap；bootstrap 限定 Mapper 扫描为已有 @Mapper 标记的接口，避免清理端口被误注册。

## Dependencies

依赖前一迁移批次；用户已指示继续迁移。

## Non-goals

不升级依赖、不改业务契约、不提交发布。
