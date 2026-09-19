## Why

学习页当前只读取本地缓存，已写入数据库的摄影课程不会显示，无法满足已登录用户打开 P0 缓存课程的交付要求。

## What Changes

- 学习页通过已认证的课程目录与详情接口加载服务端课程。
- 课程目录响应提供每门课程的课时数量，供学习页在不预载详情的情况下展示。
- 补全加载、空态、失败重试与失效详情的可见反馈。
- 保持既有本地缓存能力不作为课程目录的唯一数据源。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `learning/ai-courses`: 已登录用户可在 App 学习页查看并打开服务端缓存的课程内容。

## Dependencies

- 既有受保护的 `GET /courses` 与 `GET /courses/{courseId}` 契约；目录响应新增向后兼容的课时数量字段。

## Non-goals

- 不修改课程种子、课程生成、作业提交、进度记录、离线缓存策略或导航结构。

## Impact

- `frame-forward-app` 的 learning feature、应用装配和相关测试，以及 `frame-forward-server/course` 与 OpenAPI 课程目录契约；不新增依赖。
