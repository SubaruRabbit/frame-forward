## Why

scene-analysis、shooting-plan 的页面混杂请求、轮询与展示状态。

## What Changes

- 将请求流程下沉为 UseCase/Port，页面保留状态绑定。

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

不改变分析或方案规则。
