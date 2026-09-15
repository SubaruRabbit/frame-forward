## Why

evaluation 与 portfolio 的持久化模型和注入构造器重复，但聚合、JSON 解析和状态迁移必须继续留在业务层。

## What Changes

- 分类精简模型和 Spring Bean 构造器。
- 仅将确定性字段复制迁入 converter。
- 保持评价、分页、筛选和删除流程兼容。

## Capabilities

纯实现重构，不新增或修改产品能力规格。

## Impact

仅影响 evaluation、portfolio 生产代码与测试。

## Dependencies

依赖基础、equipment/media、shooting/generation changes。

## Non-goals

不把查询、JSON 容错、聚合或状态机迁入 MapStruct，不改变 API。
