## Why

shooting 与 generation 重复使用可变请求和实体赋值，需要在保持工作流输入语义的前提下统一精简。

## What Changes

- 分类应用 Lombok 并替换纯注入构造器。
- 使用 builder 或纯 converter 收敛确定性对象创建。
- 保留工作流输入组装与业务校验。

## Capabilities

纯实现重构，不新增或修改产品能力规格。

## Impact

仅影响 shooting、generation 生产代码与测试。

## Dependencies

依赖基础 change 及 auth/ai-workflow 模型迁移。

## Non-goals

不改变场景分析、拍摄计划、参考图生成行为或 API 契约。
