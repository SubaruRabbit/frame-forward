## 目标与范围

generation、evaluation：两个消费者及测试切换规范 ShootingPlanEntity/Mapper 引用，不改变查询及业务行为。

## 设计与顺序

跨模块类型按 foundation → consumers → finish 迁移，每批至多两个模块。规范实体承载全部原字段和 getter，根包实体暂继承以保持旧 Mapper/mock 类型兼容；新旧 Mapper 各自使用正确实体泛型，旧 Mapper 暂使用 legacyShootingPlanMapper bean 名，不使用强转。消费者切换后删除根包类型和临时 bean，不将兼容状态当作完成。

shooting 最终 Controller、Service、Business、Manager、Mapper、Entity 各归对应职责目录。请求和内部数据记录归 model.dto；业务异常独立于 Service，清除反向依赖。ShootingRepository 隔离所有 MyBatis，Manager 保留场景/方案去重及创建协调，完成回调事务和字段保持不变。

## 验收

本批目标类型/引用测试先 Red 后 Green，原测试和完整 MySQL 集成验证通过。最后一批目录零违规，Service/Business/Manager 无 Mapper/MyBatis；无旧类型 import 和临时 bean。各批执行 Spotless apply/check、QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify 与严格 OpenSpec 校验。

## 风险与回滚

风险为实体继承字段映射、Mapper bean 冲突、跨包可见性、JSON 与查询条件回归。通过元数据、扫描和现有业务/数据库测试证明行为不变。逆序恢复本批路径、类型和引用后重跑门禁，保留前序工作和用户修改，不操作真实数据或数据库结构。
