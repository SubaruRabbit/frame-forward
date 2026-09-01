## Context
目录是器材事实来源，首版不建设管理后台。
## Goals / Non-Goals
**Goals:** 稳定目录ID、精确P0数据和兼容查询。
**Non-Goals:** 用户器材和自动抓取厂商数据。
## Decisions
- `frame-forward-server/equipment` uses versioned Flyway migrations and seed files to maintain the catalog in the project-standard MySQL 8.4 database through MyBatis-Plus.
- 机身、镜头、附件使用不同实体并共享品牌标识；兼容性由卡口与画幅规则计算，不保存任意两项的矩阵。
- `contracts` 只开放只读列表与筛选接口；目录版本随响应返回。
## Risks / Trade-offs
- [型号资料错误] → 种子数据带来源与版本，修改走独立change。
- [Canon镜头被误用] → 无转接记录时跨卡口始终不兼容。
