## Why

课程进度表使用复合主键，数据库约束正确，但 MyBatis-Plus 无法将其识别为可按 ID 操作的实体，导致应用启动告警。需要统一以单列代理主键承载实体身份，并保持原有业务去重规则。

## What Changes

- 为 `lesson_progress` 引入不可变的单列 `id` 主键。
- 将原复合主键转换为唯一约束，确保同一账户、课程版本和课时仍只能有一条进度。
- 更新持久化映射与迁移验证，确保新旧数据库均可安全升级。

## Capabilities

### New Capabilities

- `data-model/primary-key-governance`: 定义持久化表主键与业务唯一约束的治理要求。

### Modified Capabilities

- 无。

## Impact

影响 `bootstrap` 的 Flyway 迁移与集成测试、`course` 的进度实体映射；不修改 API、客户端契约或既有进度语义。

## Dependencies

- MySQL 8.4 与 Flyway 迁移机制。
- 现有 `lesson_progress` 数据必须满足既有复合主键约束。

## Non-goals

- 不变更其他已具备主键的表。
- 不增加或变更对外 API。
