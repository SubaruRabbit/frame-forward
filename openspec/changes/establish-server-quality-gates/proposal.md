## Why

服务端缺少 Spotless、覆盖率和静态分析入口，现有格式与质量门禁无法阻断不合规变更。

## What Changes

- 在 Maven reactor 建立 IntelliJ 风格的 Spotless 唯一格式化入口。
- 建立 JaCoCo 与静态分析的可执行门禁。

## Capabilities

### New Capabilities

无。本 change 只建立工程门禁，设置 `skip_specs: true`。

### Modified Capabilities

无。

## Impact

影响根 POM、`bootstrap` 和质量文档，不改业务行为。

## Dependencies

`isolate-server-tests-and-secure-configuration`。

## Non-goals

- 不用降低阈值换取通过。
- 不进行全量业务重构。
