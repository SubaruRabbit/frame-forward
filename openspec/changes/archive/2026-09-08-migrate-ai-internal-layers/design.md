## 目标与范围

仅 ai-workflow。SceneAnalysisGraph、PlanGenerationGraph、PhotoEvaluationGraph、ReferenceImageGraph、CourseGenerationValidator 是确定性业务规则，归 business；本批不迁移被 evaluation 使用的 RetakeComparisonGraph。

AiTaskManager 归 manager，保留更新时间等持久化编排；AiTaskMapper 归 mapper，新建 AiTaskRepository 承接现有 selectList/selectOne/selectById/insert/updateById。查询条件和参数保持原样，Repository 不依赖 Runtime 状态类型，RUNNING 使用既有持久化字符串值。

根包 AiTaskBusiness、AiTaskRuntime、实体和回调保留到后续公共类型迁移，本批不是模块最终零违规验收。无兼容代理和重复业务实现。

## 验收

先增加目标类名/目录和 Manager 不依赖 Mapper 的失败测试，再迁移。验证运行中任务查询、幂等键三条件查询、主键访问和保存更新；原 AI 状态流转及 Graph 测试通过。执行 Spotless apply/check、完整 clean verify、严格 OpenSpec 校验，门禁基线 353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef，阈值不变。

## 风险与回滚

风险为 Spring/MyBatis 扫描和跨包引用。完整 MySQL 集成测试与内部类型断言验证。回滚仅逆向恢复本批路径、引用和 Repository 提取后重跑门禁；不修改数据库和前序用户工作。
