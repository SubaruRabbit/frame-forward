## Why

继续已确认的全模块分层迁移，完成媒体实体目录规范化。

## What Changes

建立 model.entity.MediaEntity，原根包实体暂继承目标类并保留表名映射；Manager 新增返回规范实体的 findOwnedEntity、findEntityById，复用 Repository。旧查询入口暂保留。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

frame-forward-server：media。

## Dependencies

依赖 已完成 media Service 与端口迁移，用户已授权继续全部迁移。

## Non-goals

不改表结构、SQL、权限、事务、媒体数据和 HTTP 行为，不提交发布。
