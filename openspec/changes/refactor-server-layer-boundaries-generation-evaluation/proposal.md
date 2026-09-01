## Why

generation、evaluation 直接耦合规则、流程与 Mapper，影响可测性。

## What Changes

- 按 Business/Manager 边界重构两模块。

## Capabilities

### New Capabilities

无；内部重构，`skip_specs: true`。

### Modified Capabilities

无。

## Impact

仅 generation、evaluation 内部实现与测试。

## Dependencies

C0、C1、C5、C6。

## Non-goals

不改参考图、评测或重拍语义。
