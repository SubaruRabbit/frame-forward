## 1. 根目录开发命令

- [x] 1.1 `仓库根目录`：新增仅包含项目元数据和脚本的 `package.json`，并验证 `npm run` 列出 `android` 命令。
- [x] 1.2 `仓库根目录`：将 `android` 脚本委托给 `frame-forward-app`，并验证 npm 在子项目目录解析现有脚本。

## 2. 回归验证

- [x] 2.1 `frame-forward-app`：为照片导入补充对新文件选择器调用的回归测试，并验证测试在迁移前失败。
- [x] 2.2 `frame-forward-app`：迁移到兼容 React Native 0.87 的 `@react-native-documents/picker`，保留 JPEG 选择与上传的文件字段，并验证照片导入测试通过。

## 3. Android 安装验证

- [x] 3.1 `仓库根目录`：运行 `npm run android`，验证其输出进入 React Native Android 启动流程、成功安装到已连接的模拟器，且不再出现根目录 `package.json` 缺失错误或文件选择器 Java 编译错误。
