## 1. 解析与边界基础

- [x] 1.1 在 `frame-forward-app` 配置 TypeScript/Babel/Metro/Jest 一致的 RN 专项别名并锁定 `babel-plugin-module-resolver`，以 typecheck、Jest 别名用例和 Android/iOS Metro 解析命令均通过为验收。
- [x] 1.2 在 `frame-forward-app/scripts` 实现 TypeScript AST 架构检查器及带负责人、审批证据、到期日和清理 change 的逐文件迁移清单，覆盖完整区域矩阵、公开入口、测试权限、平台图、循环和受限 SDK；以批准时既有 legacy 路径仅被精确报告、新增或扩大 legacy 路径及所有非例外违规被阻断、允许/拒绝 fixture 全部符合预期且 `npm run architecture:check` 通过为验收。

## 2. 基础能力迁移

- [x] 2.1 将 `frame-forward-app/shared` 的网络、存储、安全和日志适配迁入 `src/services` 并建立最小公开入口，更新调用方后以原网络/存储/日志测试在新入口通过且业务源码不直接导入受限 SDK 为验收。
- [x] 2.2 将 `frame-forward-app` 的 app 装配、环境配置、路由契约、session domain 与 `ScreenState` 迁入规范 `src` 区域，保持 `App.tsx` 仅调用 app 公开入口；以 app-shell、composition、session、route 和组件测试保持原行为为验收。

## 3. 门禁与交付

- [x] 3.1 将 `architecture:check` 接入 `frame-forward-app` 阻断式 `quality`，更新 README、release gates 与迁移清单，明确例外于 2026-10-12 到期、存续期间禁止合并发布且最终 change 必须清零；以 `npm run format:check`、零警告 `npm run lint`、`npm run architecture:check`、`npm run typecheck`、Jest 全部通过为验收。
- [x] 3.2 对 `frame-forward-app` 执行 Android 与 iOS 的别名打包解析及受影响平台构建，记录命令和结果；以 `npm run android:check`、`npm run ios:check` 成功、无未批准忽略或门禁降级，并明确这些结果不解除存续中的合并发布禁令为验收。
