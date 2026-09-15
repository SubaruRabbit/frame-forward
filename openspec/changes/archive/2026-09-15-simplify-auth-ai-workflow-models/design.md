## Context

auth 的 Entity 包含密码哈希、删除令牌等敏感字段，ai-workflow 同时包含可变请求、任务 Entity 和多模型路由构造器。涉及 `frame-forward-server` 的 auth、ai-workflow 模块。

## Goals / Non-Goals

**Goals:**

- 在不扩大敏感数据输出面的前提下精简模型。
- 收敛删除任务响应等纯字段映射。
- 简化纯依赖注入 Bean 与 AI 请求创建。

**Non-Goals:**

- 不改变密码策略、令牌生命周期、删除状态机、幂等逻辑或模型路由。
- 不强行删除含 `@Value` 配置参数的 `AiTaskBusiness` 显式构造器。

## Decisions

1. auth Entity 不使用 `@Data`；统一使用访问器、builder 和构造器注解，且不生成包含敏感字段的 `toString()`。
2. `AuthConverter` 仅映射 `AccountDeletionJobEntity` 与调用方提供的删除令牌到响应 record；哈希、授权与状态判断仍在业务层。
3. `AiTaskCreateRequest` 采用可变 DTO 注解组合并增加 builder；保留现有属性语义，逐步把创建调用改为 builder。
4. `AiTaskEntity` 使用 Entity 注解组合，状态更新继续由业务代码明确执行。
5. 只有纯 final 依赖赋值构造器使用 `@RequiredArgsConstructor`；配置转换和异常构造器保留。

## Risks / Trade-offs

- [敏感字段进入日志] → 禁止敏感 Entity 的自动 `toString()` 并增加反射/字符串安全测试。
- [共享 AI 请求改变跨模块编译] → 保持既有字段访问兼容，后续消费者 change 再统一 builder。
- [构造器注解影响测试直接 new] → 生成构造器保持原参数顺序，并运行现有边界测试。

## Migration Plan

1. 先补敏感字段、序列化、converter 和构造器兼容测试。
2. 迁移 auth，再迁移 ai-workflow；每个模块完成后独立测试。
3. 保留跨模块兼容访问方式直至消费者迁移结束。
4. 若回归失败，恢复相应模块的显式方法和转换，不影响基础工具链。
