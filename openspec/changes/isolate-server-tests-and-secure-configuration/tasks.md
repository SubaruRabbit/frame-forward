## 1. 安全配置与隔离测试

- [x] 1.1 `frame-forward-server/bootstrap`：为开发、测试环境拆分数据源配置并移除默认凭据；验证缺失生产凭据时启动明确失败。
- [x] 1.2 `frame-forward-server/bootstrap`：先使当前集成测试在隔离存储下稳定复现失败，再配置测试夹具；验证 `mvn -q test` 不依赖本机 MySQL。
- [x] 1.3 `frame-forward-server/auth`：调整认证/删除相关测试为受控依赖；验证正常、失败和重试路径通过。
- [x] 1.4 `frame-forward-server`：配置受版本控制的 Eclipse JDT XML 与 Spotless，覆盖所有主/测试 Java 源码；应用格式化基线并验证 `mvn spotless:check`。
- [x] 1.5 `frame-forward-server`：执行敏感信息扫描与完整 Maven 测试，记录结果并确认无默认密码。
