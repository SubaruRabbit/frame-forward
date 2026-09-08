# 验证记录

2026-09-07：迁移前上一批全 reactor 测试通过。仅切换本消费者中的 auth 引用及 Bearer 入口，迁移后完整 Maven verify 通过（基线 353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef）；Spotless apply/check 通过。源码与测试不再引用 auth 根包旧类型。未改变业务契约或质量门禁。未提交、未发布。
