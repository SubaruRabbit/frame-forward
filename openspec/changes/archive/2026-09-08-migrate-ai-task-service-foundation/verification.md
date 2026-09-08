# 规范入口基础验证

- ai-workflow 新增 service.AiTaskRuntime 与五个独立 DTO，暂委托旧 Runtime，不复制执行逻辑、事务或恢复钩子。
- Red：规范 Service 类不存在，日志 `/private/tmp/ai-service-foundation-red.log`。
- 类型、五种状态、请求/响应 JSON 等价、nullable 字段、异常原实例、SSE 与单次恢复委托测试通过；测试 Jackson 泛型显式指定 JsonNode，断言未删除。
- Spotless apply/check 通过，日志 `/private/tmp/ai-service-foundation-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，含 MySQL/Spring 新旧 bean 装配，日志 `/private/tmp/ai-service-foundation-verify.log`。
- 变更行覆盖率 93.71%、分支覆盖率 86.27%、CPD 0.00%，复杂度门禁通过；严格 OpenSpec 校验通过。
- 本批为过渡基础，不是最终结构：消费者三批切换后必须迁入实际 Runtime/Business/Controller，实现独立 DTO 与异常、恢复默认 bean 名，删除委托及旧 Runtime 嵌套类型。
- 遵守 Java 后端宪章；未提交发布。回滚仅移除本批新增文件并重跑门禁，不触碰旧 Runtime、数据库或用户修改。
