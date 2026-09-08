## 目标与范围

仅 ai-workflow。新增 model.dto.AiTaskState、AiTaskCreateRequest、AiTaskCreated、AiTaskTrace、AiTaskStatus，字段、枚举值、JSON 名称与原 Runtime 嵌套类型一致，数据类不承载业务规则。

规范 service.AiTaskRuntime 暂为薄委托：create/get 转换独立 DTO，events/recoverInterruptedTasks 委托原 Runtime，异常原样传播。规范 bean 临时命名 layeredAiTaskRuntime，旧 bean 名 aiTaskRuntime 保持不变。不复制调度、订阅、状态机，不添加第二个 PostConstruct 恢复动作。旧 Runtime 仍是唯一实际执行者，事务与异步行为不变。

## 后续顺序与删除条件

消费者分三批：course+shooting，generation+evaluation，bootstrap，各批包含测试且最多两个模块。全部切换后 AI 单模块将实际 Runtime 实现迁入 service，切换到独立 DTO、业务异常及业务数据记录，迁移 Business 和 Controller，删除本批委托、旧根包 Runtime 及嵌套类型，规范 bean 恢复默认名称。不得把临时兼容状态当作最终合规。

完成回调/Entity 另按实际消费者分批治理；全部旧类型清理后执行全模块零违规审计。Business 的认证依赖需在实际实现收尾时回归 Service，并保持先校验请求再认证、缺失任务先返回 NotFound 的现有顺序。

## 验收

反射类型回归先 Red；验证 create/get DTO 值、所有枚举值、null 请求/trace/result、JSON 等价、SSE 原实例与恢复单次委托、异常实例原样传播。执行 Spotless apply/check、完整 clean verify（基线 353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef）与严格 OpenSpec 校验。

## 风险与回滚

风险为新旧 bean 冲突、转换丢字段、重复恢复或异常改变。单元转换测试及完整 MySQL/Spring 集成验证。回滚仅删除本批新 Service/DTO/测试，不触碰现有 Runtime、数据库或用户修改，重跑门禁。
