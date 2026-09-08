## Why

继续已确认的 Java 分层迁移，消除 generation 根包堆放及 Manager 的反向服务依赖。

## What Changes

按 controller/service/business/manager/repository/mapper/model 分包，提取请求与持久化 DTO、业务异常；完成回调的媒体编排上移至 Service。

## Capabilities

无业务能力变更，纯重构。

## Impact

仅 generation 模块。

## Dependencies

依赖已完成的 media 迁移，沿用已批准的后续迁移范围。

## Non-goals

不改生成提示词、任务类型、权限、事务、SQL、表结构或 HTTP，不提交发布。
