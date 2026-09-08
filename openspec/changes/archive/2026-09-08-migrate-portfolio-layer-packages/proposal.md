## Why

继续已确认的分层迁移，消除 portfolio 根包文件堆放和 Manager 直接持久化。

## What Changes

按职责迁移 Controller、Service、Business、Manager、Mapper、Entity，提取 DTO/异常，引入 Repository，筛选规范化留在服务层。

## Capabilities

无业务能力变更，纯重构。

## Impact

仅 portfolio 模块。

## Dependencies

沿用用户已批准的后续迁移范围，依赖现有媒体和评价查询/清理端口。

## Non-goals

不修改 HTTP、分页与筛选规则、删除状态机及清理顺序、表结构或权限，不提交发布。
