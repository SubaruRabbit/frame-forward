# 消费者迁移验证

- 仅 generation、evaluation 的生产/测试切换规范 Service/DTO；旧 Runtime import/嵌套类型扫描零结果。
- Red：旧 Runtime 字段类型断言失败，日志 `/private/tmp/ai-consumers-generation-evaluation-red.log`。
- Spotless apply/check 通过，日志 `/private/tmp/ai-consumers-generation-evaluation-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，含原单元、类型回归与 MySQL 集成，日志 `/private/tmp/ai-consumers-generation-evaluation-verify.log`。
- 覆盖率、CPD、复杂度门禁、严格 OpenSpec 校验和本批 diff 空白检查全部通过。
- 遵守 Java 后端宪章，未提交发布；回滚仅恢复本批引用并重跑门禁，提供方委托待 bootstrap 切换后清理。
