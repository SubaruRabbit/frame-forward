## 1. 检查工具

- [x] 1.1 在 frame-forward-server/common 为目录与 package 检查编写合法、非法夹具测试，记录缺少检查实现时的失败证据。
- [x] 1.2 在 frame-forward-server/common 实现测试专用检查工具，验证错位路径、根包混放、角色错位均被识别，合法结构通过，执行模块测试验证 Green。
- [x] 1.3 在 frame-forward-server/common 配置测试辅助代码的复用产物，在 auth 增加测试范围引用；验证生产依赖树未增加测试工具依赖。

## 2. 接入与交付

- [x] 2.1 在 frame-forward-server/auth 接入现状检测测试，验证准确报告六个已知混放类；保留已有依赖边界断言，明确本测试不表示目录合规。
- [x] 2.2 对 frame-forward-server 各模块只读盘点跨模块引用与嵌套 DTO，输出后续每批最多两个模块的迁移清单和依赖顺序；逐项列出源目录、目标目录、消费者和验证方式。
- [x] 2.3 在 frame-forward-server 执行 mvn spotless:apply、mvn spotless:check、QUALITY_BASE_REF=<实施前基线> mvn verify，记录结果与尚未迁移模块；只有全部通过才完成本批检查基础设施，不标记总体迁移完成。
