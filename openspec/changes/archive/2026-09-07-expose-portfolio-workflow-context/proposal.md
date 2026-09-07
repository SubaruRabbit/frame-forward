## Why

作品详情的点评、方案和重拍关联目前是无结构对象，客户端无法可靠取得会话、评价和方案标识来启动已批准的复评、会话和对比流程。需要以私有、最小且受契约约束的工作流上下文替代不透明数据。

## What Changes

- 为已拥有作品详情增加类型化工作流上下文：可重分析媒体、来源方案、关联会话及该会话内可对比的已完成评价。
- 上下文只返回已认证用户拥有且未处于删除中的数据；缺失关联以明确空值或空集合表示。
- 将 OpenAPI、服务端作品集投影和 App Portfolio DTO 映射同步为该稳定结构。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `portfolio/work-browser`：已拥有作品详情暴露启动复评、会话与同会话对比所需的受限工作流上下文。

## Impact

影响 `contracts`、`frame-forward-server` 的 portfolio/evaluation 查询边界，以及 `frame-forward-app` 的 Portfolio 基础设施映射。

## Dependencies

既有媒体、评分、拍摄方案、拍摄会话和重拍关系数据；`connect-photo-review-and-shooting-session-flows` 的公开输入契约。

## Non-goals

不新增评分、会话或对比操作；不改变归属校验、评分结果或重拍规则；不返回其他用户数据。
