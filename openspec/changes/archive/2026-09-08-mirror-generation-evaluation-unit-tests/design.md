## 目标与范围

仅 generation、evaluation：generation 的 ReferenceImageServiceTest→service；evaluation 的 PhotoEvaluationServiceTest、PortfolioEvaluationQueryTest、RetakeComparisonServiceTest→service。当前生产类型已归层，单一职责单元测试应镜像对应层；架构、目录迁移、跨层组合和 bootstrap 集成测试保留根测试包。

## 验收

先增加指定单元测试规范包类名回归，迁移前 ClassNotFound 为 Red。仅改变 package、路径及 Spotless 清理的同包 import，不删改断言；目标测试全部被 Surefire 发现执行。执行 Spotless apply/check、QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify、旧测试路径检查及严格 OpenSpec 校验。

## 风险与回滚

风险为包可见测试辅助或重复类名；编译及全量测试验证。回滚仅恢复本批测试路径/package 后重跑门禁，不触碰生产与用户已有修改。
