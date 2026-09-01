## Why

仓库当前只有文档和OpenSpec配置，需要先建立可独立构建的App、服务端和契约工程，后续change才能在稳定边界内实现。

## What Changes

- 初始化 `frame-forward-app`、`frame-forward-server` 和 `contracts`。
- 建立最小构建、测试和本地配置骨架。
- 约束服务端模块化单体的基础依赖方向。

## Capabilities

### New Capabilities

无。本change仅初始化工程结构，已设置 `skip_specs: true`。

### Modified Capabilities

无。

## Impact

影响仓库根目录、三个新项目目录和基础构建文件，不实现用户功能。

## Dependencies

无。

## Non-goals

- 不实现页面、接口或AI调用。
- 不设计生产部署方案。
