## 1. 失败测试

- [x] 1.1 在 `frame-forward-server/bootstrap` 增加认证表注释迁移测试，显式覆盖三张表全部列，并验证测试在实现前因 COMMENT 缺失而失败
- [x] 1.2 在 `frame-forward-server/auth` 增加 Entity 文档源码测试，覆盖三个 Entity 全部字段的一字段一行和中文 Javadoc，并验证实现前失败

## 2. 实现

- [x] 2.1 在 `frame-forward-server/bootstrap` 新增 V18 向前迁移，为认证表及全部列写入中文 COMMENT，并验证认证表注释测试通过
- [x] 2.2 在 `frame-forward-server/auth` 为三个 Entity 全部字段增加逐字段中文 Javadoc、拆分聚合声明，并验证模块测试通过

## 3. 质量门禁

- [x] 3.1 在 `frame-forward-server` 执行 `mvn spotless:apply`、`mvn spotless:check` 和带有效 `QUALITY_BASE_REF` 的 `mvn verify`，确认全部门禁通过
