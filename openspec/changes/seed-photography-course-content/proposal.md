## Why

当前 P0 课程目录仅包含四个单课时摘要，无法满足已批准 PRD 对摄影基础、微单操作、器材和首批机型教程的完整内容与学习闭环要求。

## What Changes

- 将 P0 摄影教程的结构化正文、示例、判断题和实拍任务作为可追溯的缓存课程内容补齐并持久化。
- 扩展课程交付，使课程详情可读取完整已缓存内容，同时保留既有进度与作业反馈语义。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `learning/ai-courses`: 已缓存 P0 课程须交付完整、可追溯的结构化教学内容。

## Dependencies

- 已批准 PRD `frame-forward-prd-v1.0.0-approved`；现有课程 API、内容版本和 Flyway 迁移机制。

## Non-goals

- 不建设课程管理后台，不增加课程视频，不实现相机直连或遥控。

## Impact

影响 course 与 bootstrap 模块、课程数据库迁移、课程 API 契约及对应测试；不引入外部依赖。
