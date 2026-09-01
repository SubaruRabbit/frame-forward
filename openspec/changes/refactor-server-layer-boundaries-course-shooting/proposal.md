## Why

course、shooting 的用例编排直接混入规则和 Mapper 访问。

## What Changes

- 将两模块迁移至 Business/Manager 分层并增加架构测试。

## Capabilities

### New Capabilities

无；内部重构，`skip_specs: true`。

### Modified Capabilities

无。

## Impact

仅 course、shooting 内部实现与测试。

## Dependencies

C0、C1、C4、C5。

## Non-goals

不改变课程或拍摄方案行为。
