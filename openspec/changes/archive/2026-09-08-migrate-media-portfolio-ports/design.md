## Context

media 的作品查询及清理端口仅由 portfolio 消费，可以在两个模块内一次迁移，无须兼容入口。

## Goals / Non-Goals

查询投影在 service，纯 record 在 model.dto，跨模块清理接口在 gateway；旧根包类型全部删除。实体及其他模块仍待后续，不改变业务规则。

## Decisions

PortfolioMediaQuery 仍调用 MediaManager，保留原 Component 注册及查询、排序、EXIF 解码、空值和异常语义。嵌套 Item 改为独立 PortfolioMediaItem record，字段及 JSON 结构不变。WorkMediaCleanup 接口原样迁移，媒体服务和 portfolio 的管理器/测试同步接口类型。

## Risks / Trade-offs

需验证过滤和删除链路、接口注入及投影序列化；增加目录 Red 和投影单测，并保留现有 portfolio 业务测试。

## Migration Plan

先目录失败断言，再迁移两模块，执行 Spotless apply/check 及 QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify。回滚仅逆向恢复路径、投影类型和引用，不触碰数据库内容，重新运行门禁。
