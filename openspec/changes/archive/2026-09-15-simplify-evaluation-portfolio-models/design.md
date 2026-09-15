## Context

evaluation、portfolio 具有可变请求、多个 Entity 和大量聚合代码；其中只有一部分是一一字段复制。涉及 `frame-forward-server` 的 evaluation、portfolio 模块。

## Goals / Non-Goals

**Goals:**

- 精简模型与纯注入构造器。
- 把可证明为纯转换的代码交给 MapStruct。
- 保持评价状态、作品筛选、分页、详情和删除语义。

**Non-Goals:**

- 不迁移 JSON 解析容错、数据库查询、分页游标、状态机和跨模块聚合。
- 不改变响应 Map 结构或 API 契约。

## Decisions

1. 可变请求 DTO 使用完整 JavaBean Lombok 注解；Entity 使用 getter/setter/builder/构造器组合，不使用 `@Data`。
2. converter 方法必须输入所有确定性来源；若映射需要查询、异常恢复或当前时间，则留在 Business/Manager。
3. `PortfolioEvaluationQuery` 与 `PortfolioService` 的 JSON、筛选和响应聚合保持显式代码，仅提取可独立逐字段验证的片段。
4. 所有 converter 使用严格未映射策略并新增逐字段测试；无明确收益时不新增空壳 converter。

## Risks / Trade-offs

- [误把业务聚合当作复制] → 代码评审按纯函数边界核对，禁止 converter 依赖 Service/Manager/Repository。
- [分页或 null 响应变化] → 复用 Controller 与服务边界测试覆盖空结果、游标和缺失评价。
- [状态实体 builder 被用于更新] → 生命周期更新继续使用明确 setter/赋值，builder 只创建新实体。

## Migration Plan

1. 先补请求、Entity、聚合结果和 converter 回归测试。
2. 迁移 evaluation 并验证，再迁移 portfolio。
3. 运行依赖的 media、shooting 测试与两模块接口测试。
4. 回滚时恢复对应模块手工赋值，保留已验证的基础依赖。
