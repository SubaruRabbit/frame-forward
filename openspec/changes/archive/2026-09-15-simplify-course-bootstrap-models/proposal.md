## Why

course 的模型创建和两模块 Bean 构造器仍有样板代码，需要在最终批次统一并复核应用装配。

## What Changes

- 精简 course、bootstrap 中可安全替代的模型和构造器。
- 为课程纯字段复制建立 converter。
- 执行启动装配及全 Reactor 最终验证。

## Capabilities

纯实现重构，不新增或修改产品能力规格。

## Impact

仅修改 course、bootstrap 生产代码与测试，并运行全量门禁。

## Dependencies

依赖基础 change 及已迁移的被调用模块。

## Non-goals

不改变课程内容、生成流程、数据库装配、API 契约或部署方式。
