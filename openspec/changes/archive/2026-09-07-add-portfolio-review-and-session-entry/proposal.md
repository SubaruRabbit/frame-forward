## Why

作品详情虽已有作品、点评、方案和重拍关联信息，但没有把用户带入照片重分析、拍摄会话和对比流程的入口，所需标识无法从已拥有的作品中传递。此变更让作品集成为这些既有流程的受控起点。

## What Changes

- 在作品详情提供对已拥有媒体发起照片复评的入口。
- 从作品详情选择已拥有的拍摄方案并创建拍摄会话。
- 在同一会话的已点评作品中选择原片与重拍片，进入对比结果。
- 入口仅传递经校验的最小标识和导航意图；实际请求仍由既有流程负责。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `portfolio/work-browser`：作品详情提供复评、创建会话和同会话重拍对比的受控流程入口。

## Impact

影响 `frame-forward-app` 的 portfolio 与 App 组合/导航边界；依赖 `connect-photo-review-and-shooting-session-flows` 提供的公开流程契约，复用既有 OpenAPI，不改服务端。

## Dependencies

`connect-photo-review-and-shooting-session-flows` 变更完成后提供照片复评、会话创建和对比的公开入口；现有作品、方案及评分数据。

## Non-goals

不改变评分、会话、对比的业务规则或 API；不在作品集复制这些流程的请求与状态管理；不新增跨用户数据可见性。
