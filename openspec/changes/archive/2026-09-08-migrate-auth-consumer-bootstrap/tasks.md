## 1. 实施与验证

- [x] 1.1 确认 bootstrap 中 auth 源码及测试引用清单，验证迁移前测试。
- [x] 1.2 仅在 bootstrap 切换 AuthService、AccountDataCleanup 引用和 Bearer 解析入口，保留所有业务行为；编译验证。
- [x] 1.3 执行完整质量门禁并确认 bootstrap 不再引用 auth 根包旧入口。
