## Context

shooting 与 generation 使用可变请求、MyBatis Entity 和共享 `AiTaskCreateRequest` 构造工作流输入。涉及 `frame-forward-server` 的 shooting、generation 模块，并依赖已迁移的 ai-workflow 模型。

## Goals / Non-Goals

**Goals:**

- 使用分类后的 Lombok 注解精简模型和纯注入构造器。
- 用 builder 或纯 converter 表达确定性对象创建。
- 保持工作流输入、回调和持久化结果完全兼容。

**Non-Goals:**

- 不改变场景分析校验、拍摄计划提示词、参考图生成或任务回调协议。
- 不把 ObjectMapper、权限查询、UUID 或时间生成放入 MapStruct。

## Decisions

1. 请求 class 使用 `@Data/@Builder/@NoArgsConstructor/@AllArgsConstructor`；Entity 使用不含相等性和字符串输出的注解组合。
2. 将 `AiTaskCreateRequest` 的逐字段赋值改为 builder，但 input map 的业务构造仍由现有 Business/Service 完成。
3. 仅当存在至少两处纯字段复制时建立模块 converter；单次状态赋值优先使用 builder，避免无收益的抽象。
4. completion processor、controller、service、manager、repository 的纯注入构造器统一使用 `@RequiredArgsConstructor`。

## Risks / Trade-offs

- [builder 默认值与原始赋值不同] → 测试 null、false、空集合和完整请求。
- [工作流 map 内容意外变化] → 对 operationType 和 input map 做精确回归断言。
- [过度使用 converter 隐藏业务含义] → 只迁移一一字段复制，业务派生值继续显式传入。

## Migration Plan

1. 先写请求反序列化、任务输入和回调持久化测试。
2. 先迁移 shooting，再迁移 generation，保持共享 DTO 兼容。
3. 删除已替代的显式构造器和赋值代码。
4. 运行两模块及 ai-workflow 相关测试；按模块独立回滚。
