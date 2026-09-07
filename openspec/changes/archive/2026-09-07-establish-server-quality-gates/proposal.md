## Why

服务端缺少 Spotless、覆盖率和静态分析入口，现有格式与质量门禁无法阻断不合规变更。

## What Changes

- 在 Maven reactor 建立以受版本控制的 Eclipse JDT XML 为基线的 Spotless 唯一格式化入口。
- 建立 JaCoCo、静态分析与基于 Git 基线比较的变更代码覆盖率可执行门禁，并安全解析标准 JaCoCo XML 报告。

## Capabilities

### New Capabilities

无。本 change 只建立工程门禁，设置 `skip_specs: true`。

### Modified Capabilities

无。

## Impact

影响根 POM、`bootstrap` 和质量文档；为使既有代码满足新增的复杂度门禁，最小范围内重构 `auth` 的 `PasswordPolicy.validate` 与 `media` 的 `MediaService.ingest`，不改变业务行为。

## Dependencies

`isolate-server-tests-and-secure-configuration`。

## Non-goals

- 不用降低阈值换取通过。
- 不进行全量业务重构。
- 不修改 `PasswordPolicy.validate` 的密码规则、对外异常类型或返回语义。
- 不修改 `MediaService.ingest` 的媒体校验、持久化、派生文件或异常语义。
- 不因兼容 JaCoCo XML 而允许质量报告解析器访问外部 DTD 或 schema。
