## 1. 实施与验证

- [x] 1.1 先为 auth 目标目录和持久化边界补失败测试，记录 Red。
- [x] 1.2 在 auth 将 Entity、Mapper、Repository、Manager、Business、Service 与公开 DTO 放入目标目录，保持认证、刷新、撤销、删除状态语义。
- [x] 1.3 在 auth 保留仅供消费者过渡的旧 AuthService、AccountDataCleanup 入口，复用同一实现与 Spring 实例；AuthController 暂留原包。
- [x] 1.4 修正 bootstrap 的 Mapper 扫描范围并补回归断言，验证 auth 回归、全 reactor 编译与完整质量门禁，记录兼容入口待删除清单。
