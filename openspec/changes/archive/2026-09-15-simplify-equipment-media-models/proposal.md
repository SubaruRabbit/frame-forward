## Why

equipment 与 media 存在重复模型构造、访问器和 Entity 到 DTO 转换，适合作为 Lombok、MapStruct 首批迁移验证。

## What Changes

- 分类精简两模块 DTO、Entity 与 Spring Bean 构造器。
- 新增纯转换 converter，替换手工字段复制。
- 保持 MyBatis、Jackson 与接口输出兼容。

## Capabilities

纯实现重构，不新增或修改产品能力规格。

## Impact

仅影响 equipment、media 生产代码与测试。

## Dependencies

依赖 `establish-server-model-codegen-foundation`。

## Non-goals

不改变器材兼容规则、媒体存储流程、API 契约或字段可见性。
