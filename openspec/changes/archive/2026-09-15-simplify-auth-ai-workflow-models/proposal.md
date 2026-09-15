## Why

auth 与 ai-workflow 的可变模型和注入构造器样板较多，且认证敏感字段需要受控的 Lombok 策略。

## What Changes

- 精简两模块 DTO、Entity 与纯注入构造器。
- 引入认证纯字段 converter，并用 builder 简化 AI 请求创建。
- 阻止敏感字段进入自动字符串输出。

## Capabilities

纯实现重构，不新增或修改产品能力规格。

## Impact

仅影响 auth、ai-workflow 生产代码与测试。

## Dependencies

依赖 `establish-server-model-codegen-foundation`。

## Non-goals

不改变认证、删除任务、AI 状态机、模型路由或外部契约。
