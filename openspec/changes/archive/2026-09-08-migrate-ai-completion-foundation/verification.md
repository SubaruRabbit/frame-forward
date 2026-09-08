# 完成回调基础验证

- 新增规范 gateway/身份上下文 DTO；Manager 协调回调筛选，Business 保持状态规则。旧桥接保留 @Transactional，消费者无改动。
- Red：规范 gateway 不存在，日志 `/private/tmp/ai-completion-foundation-red.log`。
- 回调筛选、上下文、结果对象身份及异常传播测试通过，旧消费者在完整 MySQL/Spring 集成测试中仍正常执行。
- Spotless apply/check 通过，日志 `/private/tmp/ai-completion-foundation-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，日志 `/private/tmp/ai-completion-foundation-verify.log`；行覆盖率 93.01%、分支覆盖率 85.53%、CPD 0.00%，复杂度门禁通过。
- 严格 OpenSpec 校验通过。遵守 Java 后端宪章，未提交发布；按设计逆序恢复可回滚。
- 桥接和旧实体必须在消费者切换后的 AI 收尾批次删除，不作为最终结构保留。
