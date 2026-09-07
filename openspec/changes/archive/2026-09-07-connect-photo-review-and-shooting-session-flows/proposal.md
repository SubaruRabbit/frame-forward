## Why

照片点评和拍摄会话组件尚未接入既有服务端流程，用户无法从评分结果重分析照片，或建立会话并获得前后对比。

## What Changes

- App 接入既有照片点评重分析任务、拍摄会话创建和重拍对比接口。
- 页面以显式加载、成功、失败与重试状态呈现流程。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `evaluation/photo-evaluation`：客户端可从点评结果触发重分析。
- `shooting/retake-comparison`：客户端可创建拍摄会话并查询同会话作品对比。

## Impact

影响 `frame-forward-app` 的 photo-review、shooting-session 及 App 组合根；复用现有 OpenAPI，不修改服务端模块。

## Dependencies

既有照片点评、拍摄会话和重拍对比 API。

## Non-goals

不修改评分、会话或对比业务规则，不新增服务端接口。
