## 1. APP 质量门禁

- [x] 1.1 `frame-forward-app`：建立受版本控制的 `.editorconfig`、Prettier scope/ignore、ESLint/Prettier 兼容配置和 `format`、`format:check`、零警告 `lint`、`lint:fix`、`quality` 命令；新增 Node 22 的 APP CI 质量工作流。验证 `npm run format:check`、`npm run lint`、`npm run typecheck`、`npm test -- --runInBand --no-watchman` 与 `npm run quality` 均通过，且 CI 使用 `npm ci` 后阻断任一失败。
- [ ] 1.2 `frame-forward-app/shared`：补齐共享 UI 的可访问名称、语义和字号验证；验证组件测试与人工检查。
- [ ] 1.3 `frame-forward-app/android`：建立受控构建与发布检查；验证 Android 构建可追溯。
- [ ] 1.4 `frame-forward-app/ios`：建立 iOS 构建检查；验证 iOS 配置和构建可执行。
