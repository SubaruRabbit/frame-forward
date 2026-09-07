## Why

photo-review、shooting-session 的状态和交互边界需统一为可测的显式模型。
## What Changes

- 将状态转换与副作用迁移到 Application 层。
## Capabilities
### New Capabilities

无；内部重构，`skip_specs: true`。
### Modified Capabilities

无。
## Impact

仅两个 APP Feature。
## Dependencies

C2、C3。
## Non-goals

不改变评分或会话业务规则。
