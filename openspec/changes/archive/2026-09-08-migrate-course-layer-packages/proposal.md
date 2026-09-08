## Why

继续已确认的 Java 分层迁移，消除 course 根包堆放及 Manager 直连 Mapper。

## What Changes

课程类迁入 controller/service/business/manager/repository/mapper/model/component；提取 DTO 和业务异常，同步 bootstrap 引用。

## Capabilities

无新增或修改业务能力，纯重构。

## Impact

仅 course、bootstrap 两个服务端模块。

## Dependencies

依赖已完成的 media 分层，用户已确认执行后续迁移。

## Non-goals

不改课程内容、表结构、HTTP 行为、权限、事务、AI 参数，不提交发布。
