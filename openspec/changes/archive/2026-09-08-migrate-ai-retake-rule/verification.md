# 重拍规则迁移验证

- ai-workflow 的 RetakeComparisonGraph 及镜像测试迁入 business；evaluation 的 Service/测试使用规范引用，无旧根包入口。
- Red：evaluation graph 字段类型断言失败，日志 `/private/tmp/ai-retake-red.log`。
- 原归因测试及新增空值、非法列表、问题去重、全部改善测试通过；旧 import 扫描零结果。
- Spotless apply/check 通过，日志 `/private/tmp/ai-retake-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，含 MySQL 集成测试，日志 `/private/tmp/ai-retake-verify.log`。
- 变更行覆盖率 93.61%、分支覆盖率 86.19%、CPD 0.00%，复杂度门禁通过。
- 严格 OpenSpec 校验、本批 `git diff --check` 通过。遵守 Java 后端宪章；未提交、未发布；回滚仅恢复本批路径和引用后重跑门禁。
- AI 公共 Runtime/DTO、Business、Controller/异常映射、Entity/回调仍待后续迁移。
