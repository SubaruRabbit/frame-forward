# 验证记录

2026-09-07：用户指示继续迁移后实施。目录回归先失败（缺少 model/entity/AccountEntity.java），迁移后通过。bootstrap 扫描回归先失败（annotationClass 默认为 Annotation），限定 @Mapper 后通过。

新增认证边界与持久化测试覆盖登录、注销、刷新过期/重复消费、密码更新、删除令牌与清理失败。首次完整门禁行覆盖率 87.67% 未达标，补充真实持久化边界测试后重跑；最终完整 `mvn verify` 成功，变更行覆盖率 94.27%、分支 96.59%、CPD 0.00%。基线 353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef。Spotless apply/check 通过，未降低门禁。

迁移期保留根包 AuthService 与 AccountDataCleanup 两个兼容入口，AuthController 和 AuthExceptionHandler/ProtectedResourceController 尚待最终批次迁移；消费者切换后由 finish-auth-layer-packages 删除兼容入口。此状态不代表 auth 目录已零违规。
