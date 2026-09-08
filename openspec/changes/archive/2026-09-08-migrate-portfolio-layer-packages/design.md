## 目标与范围

portfolio 全部生产 Java 文件按 controller/service/business/manager/repository/mapper/model 分包，无根包兼容入口，不修改其他模块。

## 设计

- 两个 Controller 和异常处理器归 controller；Service、Business、Manager 各归职责目录，实体归 model.entity，Mapper 归 mapper。
- Filter、Page、Favorite、DeletionJob、FavoriteRequest 独立放 model.dto；NotFound 独立为 business.PortfolioNotFound，去除 Business 对 Service 的反向引用。
- Filter 仅承载字段；原 normalized/blank 逻辑移至 Service 的 safeFilter，保留默认 20、范围 1–100、空白 camera/lens 转 null 和其他字段语义，调用方使用 record accessor。
- PortfolioRepository 隔离收藏及删除任务的 Mapper/查询，Manager 保留查找后创建、收藏更新和跨清理端口的协调。严格保留 evaluation → favorite → media 清理顺序、状态写入顺序和失败可重试记录。

## 验收

目录检查零违规；Business/Manager 无 MyBatis/Mapper 及反向 Service 依赖；原有收藏、删除成功/失败与分页测试继续通过。补充 DTO 纯数据、筛选边界、Controller JSON/状态码、删除重试顺序及 Repository 所有权过滤测试。执行 Spotless apply/check、完整 clean verify 和严格规格校验。

## 风险与回滚

敏感风险在所有权过滤与关联数据删除，不能改变 SQL 条件或吞掉失败状态。使用查询条件断言、正常/失败/重试调用顺序和 MySQL 集成测试验证。逆序恢复本批类型/路径/引用后重跑门禁，不执行真实用户数据删除，不改数据库结构，保留用户现有变更。
