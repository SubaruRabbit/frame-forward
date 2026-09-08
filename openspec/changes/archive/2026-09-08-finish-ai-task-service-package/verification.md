# AI 实际入口收尾验证

## 完成范围

- 仅 ai-workflow：实际 Runtime 迁入 service，Business 归 business，Controller/异常映射归 controller；请求/响应/状态/Creation 均为独立 DTO，业务异常独立。
- 删除根包 Runtime、临时 layeredAiTaskRuntime bean 名和 delegate 转换。恢复默认 bean 名，只有实际 Runtime 执行 PostConstruct 恢复。
- 认证回归 Service，保留请求校验先于认证、缺失任务先于认证返回 NotFound 的既有顺序；Business 不再引用 AuthService/Runtime/Mapper。
- 原 14 个 Runtime 测试迁至 service 镜像包并保留业务断言。临时 Adapter 测试替换为实际入口契约测试，覆盖状态 JSON、null 字段、异常原样传播、恢复及事务提交后调度。

## 验证

- Red：旧根包 Runtime 文件仍存在，清理断言失败，日志 `/private/tmp/ai-service-finish-red.log`。
- Spotless apply/check 通过，日志 `/private/tmp/ai-service-finish-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，含全模块和 MySQL/Spring 集成测试，日志 `/private/tmp/ai-service-finish-verify.log`。
- 变更行覆盖率 93.04%、分支覆盖率 85.53%、CPD 0.00%，复杂度门禁通过。
- 无旧 Runtime import、嵌套类型引用和临时 bean 名；全服务端根包清单仅剩 AI 的 AiTaskEntity/AiTaskCompletionProcessor，以及允许保留的 bootstrap 启动类。
- 本轮范围 `git diff --check`、严格 OpenSpec 校验通过。

## 剩余与回滚

遵守 Java 后端宪章；未提交、未发布，人工评审和发布验收未执行。回调与实体边界仍待下一批迁移，不能宣称全目标完成。回滚仅恢复本批实际实现、旧入口与委托、对应测试后重跑门禁，不操作数据库、前序消费者或用户修改。
