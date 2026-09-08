## Why

承接已确认的 Java 目录分层迁移，消除 equipment 根包堆叠及 Manager 直接访问 Mapper。

## What Changes

- equipment 按职责迁移目录、提取 DTO 与业务异常、隔离持久化；shooting 同步引用。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

frame-forward-server：equipment、shooting。

## Dependencies

依赖已完成 auth 迁移及目录检查器，用户已确认继续其他模块。

## Non-goals

不修改 HTTP、数据、权限和器材规则，不升级依赖、不提交发布。
