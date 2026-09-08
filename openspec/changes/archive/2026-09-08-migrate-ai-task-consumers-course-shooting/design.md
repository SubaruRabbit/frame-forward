## 目标与范围

仅 course、shooting。将 com.frameforward.ai.AiTaskRuntime 引用改为 service.AiTaskRuntime，嵌套 CreateRequest/Created/State/Status/Trace 对应改为 model.dto 的 AiTaskCreateRequest/AiTaskCreated/AiTaskState/AiTaskStatus/AiTaskTrace。保持调用方法、参数、字段、mock 行为和断言含义不变。

规范入口的基础委托已单独验证；本批不更改提供方。完成回调和实体引用在独立后续批次处理。

## 验收

先用字段类型回归测试证明当前仍使用旧 Runtime，迁移后规范类型断言通过。原单元与完整 MySQL/Spring 集成测试通过，目标模块不存在旧 Runtime import 或嵌套类型引用。执行 Spotless apply/check、QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify、严格 OpenSpec 校验。

## 风险与回滚

风险为注入类型、DTO 序列化和测试 mock 泛型回归；不改变 HTTP、事务、查询和任务行为。逆向恢复本批引用与测试后重跑门禁，不触碰提供方实现、数据库及用户修改。
