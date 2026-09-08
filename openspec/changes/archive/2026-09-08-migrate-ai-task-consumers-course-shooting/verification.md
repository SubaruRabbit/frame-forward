# 消费者迁移验证

- 仅 course、shooting 的生产/测试切换规范 Service/DTO；旧 Runtime import/嵌套类型扫描零结果。
- Red：旧 Runtime 字段类型断言失败，日志 `/private/tmp/ai-consumers-course-shooting-red.log`。
- Spotless apply/check 通过，日志 `/private/tmp/ai-consumers-course-shooting-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，含原单元/类型回归及 MySQL 集成测试，日志 `/private/tmp/ai-consumers-course-shooting-verify.log`。
- 行覆盖率 93.71%、分支覆盖率 86.27%、CPD 0.00%，复杂度门禁通过；严格 OpenSpec 校验与本批 diff 空白检查通过。
- 遵守 Java 后端宪章，未提交发布；回滚仅逆向恢复本批引用后重跑门禁。AI 提供方过渡委托须在其余消费者切换后清理。
