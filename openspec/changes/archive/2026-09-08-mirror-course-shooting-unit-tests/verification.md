# 单元测试镜像验证

- CourseRulesTest→business；SceneAnalysisServiceTest/ShootingPlanServiceTest→service，三个测试主体去除 package/import/空白后的 SHA-256 前后一致。
- Red：规范测试类不存在，日志 `/private/tmp/mirror-course-shooting-red.log`。
- 新包下三个原类各执行 3 个测试，全部通过；旧测试路径已不存在。
- Spotless apply/check 与 `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，日志 `/private/tmp/mirror-course-shooting-format.log`、`/private/tmp/mirror-course-shooting-verify.log`。
- 完整门禁行覆盖率 93.03%、分支覆盖率 85.53%、CPD 0.00%，复杂度通过；严格 OpenSpec 校验通过。
- 遵守 Java 后端宪章，无生产改动、未提交发布；逆向恢复本批测试 package/路径后重跑门禁即可回滚。
