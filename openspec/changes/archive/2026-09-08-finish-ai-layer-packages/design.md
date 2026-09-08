## 目标与范围

仅 ai-workflow：删除旧回调桥接，AiTaskEntity 迁入 model.entity，启用全模块目录零违规和架构依赖检查。

## 设计与顺序

当前 generation、shooting 回调只读取任务 id/accountId。规范 gateway 抽象回调接受不可变 AiTaskCompletionContext(taskId, accountId)，不暴露持久化实体。Manager 按 operationType 选择回调、传递同一个 result Map，Business 仍负责成功/失败规则和状态转换；回调异常原样向 Business 传播，不改变运行错误处理。

基础批次旧根包抽象回调继承规范抽象类，通过上下文创建仅供旧消费者读取 id/accountId 的临时实体。桥接 complete(Context, Map) 必须保留 @Transactional，防止内部调用旧方法绕开事务代理；两消费者原有 @Transactional 不删除。不使用强转，不复制业务逻辑。

消费者批次切换规范方法和上下文 accessor；原事务、所有权、幂等与 result 字段断言保持。最后 AI 单模块删除旧桥接和根包实体，把实体字段/映射原样迁至 model.entity，内部 Mapper/Repository/Manager/Business/Service/测试同步引用。

最终 common 测试检查工具接入 AI，目录零违规；Business 不直接依赖回调 gateway/Service/Mapper，Manager 不依赖 Business/Service/MyBatis，规范 gateway 不依赖 Entity。不得将过渡桥接视为最终合规。

本批同时将 AI 的 CourseGenerationValidatorTest、PhotoEvaluationGraphTest、PlanGenerationGraphTest、ReferenceImageGraphTest 镜像迁入 business 测试目录，保留所有断言。跨层架构/迁移测试保留模块根测试包。

## 验收

本批目标类型/方法/目录回归先 Red 后 Green。验证回调筛选、上下文值、结果对象相同、异常传播、事务注解、消费者原行为；最终验证实体表名、主键、字段映射及 getter。执行 Spotless apply/check、QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify、旧引用审计和严格 OpenSpec 校验。

## 风险与回滚

风险为事务代理失效、跨模块签名、实体映射或回调漏注册。通过定向测试及完整 MySQL/Spring 集成验证。逆序恢复本批路径、签名、协调位置和测试后重跑门禁，不触碰数据库或前序用户工作。
