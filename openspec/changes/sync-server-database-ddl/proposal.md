## Why

服务端课程交付表的账户字段与 `accounts.id` 类型不一致，且缺少账户级清理所需的外键约束，导致数据库结构与当前账户数据生命周期规则不同步。现在补齐迁移，保证空库初始化和既有数据库升级使用同一套结构约束。

## What Changes

- 将课程交付表的 `account_id` 统一为 `CHAR(36)`，并统一表的 InnoDB、字符集和排序规则。
- 为课程交付表补充账户外键，账户删除前由现有清理流程安全清理相关记录。
- 新增向后兼容的 Flyway 迁移，不修改既有迁移文件。

## Capabilities

### New Capabilities

本变更仅同步数据库结构，不引入新的产品能力。

### Modified Capabilities

无。

## Impact

影响 `frame-forward-server/bootstrap` 的 Flyway 迁移及课程交付表。应用 API、业务逻辑和客户端契约不变。

## Dependencies

- 依赖现有 V1、V7 和 V16 Flyway 迁移及 `AccountDatabaseCleanup` 清理顺序。

## Non-goals

- 不新增课程业务字段、API 或客户端行为。
- 不修改历史迁移、不迁移业务数据、不改变账户删除流程。
