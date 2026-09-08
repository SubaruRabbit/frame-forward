# 媒体实体基础迁移验证

依 Java 后端工程宪章新增 model.entity.MediaEntity，原字段/构造器/getter 原样迁入，根包兼容实体仅继承及委托构造。规范查询返回规范实体，复用同一 Repository，无强转和规则复制。

目标类及查询断言先 Red（`/private/tmp/media-entity-red.log`）；迁移后表名、主键、全部列及查询同实例断言通过。完整 MySQL 集成测试证明继承字段的读写映射可用。

Spotless apply/check 和基线 353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify 全 reactor 通过，日志 `/private/tmp/media-entity-format.log`、`/private/tmp/media-entity-verify.log`。OpenSpec strict validate 通过。旧实体/查询入口尚待后续消费者切换后删除，未提交发布。
