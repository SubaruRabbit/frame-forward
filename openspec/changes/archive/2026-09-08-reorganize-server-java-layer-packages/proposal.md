## Why

服务端业务根包混放各层 Java 类，现有架构测试没有约束目录。先建立可复用检查，再分模块迁移。

## What Changes

- 本批仅在 common 建立包目录检查工具，在 auth 测试中接入并记录现状；后续批次落实迁移。
- 制定各模块目录映射与迁移顺序。

## Capabilities

### New Capabilities

无；工程结构治理，使用 skip_specs。

### Modified Capabilities

无；保持业务契约。

## Impact

本批涉及 frame-forward-server 的 common、auth 测试与构建配置。

## Dependencies

遵守 Java 宪章；规格确认后实施。后续迁移依赖本批检查工具。

## Non-goals

本批不迁移生产类、不改变 API、数据库或依赖版本，不宣称全部模块已治理完成。
