## Why

继续已确认的目录迁移，整理 bootstrap 的配置及数据库清理实现。

## What Changes

- 配置移至 config，数据库清理实现移至 repository，启动类保留根包并建立精确目录断言。

## Capabilities

### New Capabilities

无，纯重构。

### Modified Capabilities

无。

## Impact

仅 frame-forward-server 的 bootstrap。

## Dependencies

auth 清理端口已迁移；本批不依赖其他业务目录，可以从清单末尾独立提前执行。用户已确认继续迁移。

## Non-goals

不修改删除 SQL、执行顺序、扫描范围和外部行为，不提交发布。
