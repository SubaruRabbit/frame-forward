## Why

在仓库根目录执行 `npm run android` 时，npm 找不到 `package.json`，导致开发者无法按仓库入口启动 Android 应用。需要提供明确且可重复的根目录命令入口。

## What Changes

- 增加根目录 npm 清单，并将 `android` 命令转发至 `frame-forward-app`。
- 不改变应用内已有的 React Native Android 启动脚本。
- 将不兼容 React Native 0.87 的旧文件选择器迁移到其维护中的替代包，并保持 JPEG 导入行为。

## Capabilities

### New Capabilities

无。本变更仅提供开发工具入口，不引入产品行为规格。

### Modified Capabilities

无。

## Impact

影响仓库根目录的 npm 命令解析、`frame-forward-app` 的本地启动入口、文件选择器依赖及照片导入调用；不涉及 API 或服务端。

## Dependencies

- `frame-forward-app/package.json` 中现有的 `android` 脚本。
- `@react-native-documents/picker` 对 React Native 0.87 的兼容支持。

## Non-goals

- 不修改 React Native、Gradle 或 Android SDK 配置。
- 不启动、安装或配置 Android 模拟器与物理设备。
