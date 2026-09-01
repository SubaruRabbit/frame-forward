## 1. APP 基础设施边界

- [ ] 1.1 `frame-forward-app/shared`：实现可注入 Network Layer、错误类型和安全存储 Port；验证单元测试覆盖认证、超时、取消和错误映射。
- [ ] 1.2 `frame-forward-app/app`：建立环境配置与 Composition Root 装配；验证 URL 不再由业务页面硬编码。
- [ ] 1.3 `frame-forward-app/shared`：保留并测试 Keychain 凭据实现，增加脱敏日志边界；验证敏感字段不进入普通存储或日志。
- [ ] 1.4 `frame-forward-app`：执行 lint、typecheck 和 Jest；验证依赖边界检查通过。
