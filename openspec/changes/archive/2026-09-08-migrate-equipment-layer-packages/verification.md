# equipment 迁移验证

- 已按 Java 后端工程宪章完成 equipment 35 个生产类型分层，shooting 同步 Service/DTO 引用；原根包无生产文件，Mapper 均保留注解。
- 独立 DTO 和业务异常消除 Business 到 Service 的反向引用；EquipmentRepository 封装查询，Manager 协调主相机清除与设置。
- Red：目录、DTO 与边界测试在旧结构下失败，日志 `/private/tmp/equipment-red.log`。
- Green：equipment 及 shooting 依赖链测试通过，日志 `/private/tmp/equipment-green.log`。
- 额外单测验证正常/异常器材请求、查询、主相机账户过滤与更新顺序、组合以及 HTTP 响应映射。首次完整门禁行覆盖率 86.40% 未通过，补足控制器映射测试后恢复通过；未调整门禁。
- 最终执行 `mvn spotless:apply spotless:check`、`QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify`：163 个测试，零失败、错误、跳过；变更行覆盖率 95.18%，分支 96.27%，CPD 0.00%，PMD 通过。
- 最终日志：`/private/tmp/equipment-format.log`、`/private/tmp/equipment-verify.log`。OpenSpec strict validate 通过，任务范围 diff --check 通过。
- 全局 diff --check 发现本轮开始前已有 formatter XML 第 27 行尾随空白，未修改该用户文件；不影响 Maven 门禁。
- 未提交、未发布；回滚按 design 逆向恢复本批路径及引用后重跑门禁。其余业务模块尚未完成分层。
