## 1. Feature 边界迁移

- [x] 1.1 `frame-forward-app/features/portfolio`：抽取 UseCase/Port；验证组件和失败删除测试通过。
- [x] 1.2 `frame-forward-app/features/reference-image`：抽取生成/轮询 UseCase；验证取消、失败、重试测试通过。
- [x] 1.3 `frame-forward-app`：执行 lint、typecheck、Jest 和边界检查；验证页面无 fetch/Keychain 依赖。
