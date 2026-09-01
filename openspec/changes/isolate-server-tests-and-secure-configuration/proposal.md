## Why

服务端测试依赖本机 MySQL，且仓库配置含默认数据库凭据，违反可重复测试和凭据保护红线。

## What Changes

- 移除默认敏感凭据，建立安全的环境配置与测试配置。
- 使 auth、bootstrap 的测试无需外部数据库即可稳定执行。

## Capabilities

### New Capabilities

无。本 change 仅整改工程实现，设置 `skip_specs: true`。

### Modified Capabilities

无。

## Impact

影响 `bootstrap`、`auth`、测试资源和 Maven 测试配置；保持 API 与业务规则不变。

## Dependencies

合规基线 C0。

## Non-goals

- 不修改认证或账号删除的对外语义。
- 不引入生产凭据。
