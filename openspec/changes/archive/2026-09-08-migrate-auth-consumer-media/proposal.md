## Why

将 media 切换到 auth 目标包，落实已确认的服务端目录迁移计划。

## What Changes

- 只修改 auth、media 模块；保持 HTTP、数据库与权限行为。

## Capabilities

### New Capabilities

无，纯工程重构，skip_specs。

### Modified Capabilities

无。

## Impact

frame-forward-server：auth、media。

## Dependencies

依赖前一迁移批次；用户已指示继续迁移。

## Non-goals

不升级依赖、不改业务契约、不提交发布。
