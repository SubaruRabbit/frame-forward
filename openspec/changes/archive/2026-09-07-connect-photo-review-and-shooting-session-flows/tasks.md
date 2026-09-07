## 1. 照片点评流程

- [x] 1.1 `frame-forward-app/features/photo-review`：实现重分析 Port、UseCase 和 UI state；验证成功、失败保留结果与重试测试。

## 2. 拍摄会话与对比流程

- [x] 2.1 `frame-forward-app/features/shooting-session`：实现创建会话与读取对比的 Port、UseCase 和 UI state；验证成功、失败与重复操作测试。

## 3. 组合与质量

- [x] 3.1 `frame-forward-app/app`：装配两个网络 Port 并验证 Presentation 无直接网络依赖。
- [x] 3.2 `frame-forward-app`：执行 `npm run quality`，验证格式、静态检查、类型检查和 Jest 通过。
