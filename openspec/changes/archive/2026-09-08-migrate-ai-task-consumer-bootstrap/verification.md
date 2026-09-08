# bootstrap 消费者验证

- 恢复集成测试切换规范 service.AiTaskRuntime，提供方未修改。
- Red：字段类型断言失败，日志 `/private/tmp/ai-consumer-bootstrap-red.log`。
- Spotless apply/check 通过，日志 `/private/tmp/ai-consumer-bootstrap-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，包含恢复、HTTP/SSE 和 MySQL 集成，日志 `/private/tmp/ai-consumer-bootstrap-verify.log`。
- 行覆盖率 93.71%、分支覆盖率 86.27%、CPD 0.00%，复杂度门禁通过；严格 OpenSpec 校验通过。
- 五个外部消费者均无旧 Runtime import/嵌套 DTO 引用；提供方可以进入删除委托和旧类型的收尾批次。
- 遵守 Java 后端宪章，未提交发布；回滚恢复本批测试注入与引用后重跑门禁。
