## 目标与范围

ai-workflow 的 RetakeComparisonGraph 根据主要问题证据判断改善，属于 business。移动生产类及其镜像单元测试；evaluation 的 RetakeComparisonService 及测试改用规范引用。两模块外没有此类型的 Java 消费者，不需要临时适配。

## 验收

先用 evaluation 反射测试证明 graph 字段尚未使用规范业务类型；迁移后通过。保留原有分数不代表改善、已解决问题归因测试，并覆盖空/非法问题列表和全部改善。原行为、完整 MySQL 集成测试与质量门禁必须通过。

执行 Spotless apply/check、QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify、旧引用扫描和严格 OpenSpec 校验。

## 风险与回滚

风险为 Spring 注入与跨包引用，完整扫描/测试验证。不改变 HTTP、JSON、事务或数据库；回滚仅逆向恢复本批路径、package、import 与测试后重跑门禁。
