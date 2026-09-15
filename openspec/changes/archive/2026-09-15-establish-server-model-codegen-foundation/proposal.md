## Why

服务端缺少统一的 Lombok 与 MapStruct 编译基线，无法安全、分批地消除模型和依赖注入样板代码。

## What Changes

- 在父 Maven 配置中引入并固定 Lombok、MapStruct 和处理器绑定。
- 启用 Spring component model 与未映射目标字段失败策略。
- 增加注解处理器编译冒烟验证。

## Capabilities

纯工具链重构，不新增或修改产品能力规格。

## Impact

影响 `frame-forward-server/pom.xml` 与编译测试，不改变 API 或运行时业务行为。

## Dependencies

无；后续模型简化 changes 依赖本 change。

## Non-goals

不迁移业务模块模型，不调整依赖版本基线之外的第三方库。
