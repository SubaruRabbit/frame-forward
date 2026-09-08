# 验证记录

规范方案实体、Mapper 已建立，旧实体暂继承规范实体，旧 Mapper 临时命名 legacyShootingPlanMapper。实体表、主键、全部字段和 Mapper 泛型断言通过；完整 MySQL 集成验证证明扫描及原读写链路可用。

- Red：`/private/tmp/shooting-foundation-red.log`。
- Spotless apply/check：`/private/tmp/shooting-foundation-format.log`。
- 全 reactor clean verify：`/private/tmp/shooting-foundation-verify.log`，基线 353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef，BUILD SUCCESS。
- 变更行覆盖率 94.60%，分支覆盖率 88.43%，CPD 0.00%。

临时类型必须由 finish-shooting-layer-packages 删除，整体迁移未完成；未提交发布。
