# FrameForward 客户端

## 项目结构迁移

应用编排位于 `src/app`，领域状态位于 `src/domains`，复用 UI 位于 `src/components`，外部 I/O 适配器位于 `src/services`。跨边界依赖统一使用 TypeScript、Babel、Jest 和 Metro Babel 管线共同配置的路径别名。

所有功能模块均位于 `src/features/**` 并通过各模块 `index.ts` 暴露公共入口。根目录 `features/**` 源文件永久禁止，`npm run architecture:check` 会在 Android 与 iOS 依赖图中严格拒绝旧结构和跨边界深层导入。

运行 `npm run architecture:test` 验证独立正反例夹具，运行 `npm run architecture:check` 验证 Android 与 iOS 项目依赖图。`npm run quality` 已包含项目架构检查。

FrameForward 客户端是“下一张 FrameForward”的 React Native 应用，当前以 Android 为首要目标平台。它为摄影学习、器材管理、现场分析、拍摄方案、作品点评和重拍比较提供移动端入口。

## 技术栈

- React Native 0.87
- React 19
- TypeScript
- Jest、ESLint 和 Prettier

## 环境要求

- Node.js 22.11 或更高版本
- npm
- Android Studio、Android SDK 和已启动的 Android 模拟器或已连接设备
- JDK（供 Android Gradle 构建使用）

iOS 代码保留在仓库中；如需运行 iOS，还需要 macOS、Xcode、CocoaPods 和 Ruby Bundler。

## 本地环境变量

常规 Android 开发不需要客户端 dotenv 配置。Android Release 构建所需的签名变量统一维护在仓库根目录的[`.env.example`](../.env.example)中：

```sh
# 在仓库根目录执行一次
cp .env.example .env
```

执行 Release 构建前，在仓库根目录根据当前终端导出变量到当前进程：

### Linux / macOS（bash、zsh 等 POSIX shell）

```sh
set -a && . ./.env && set +a
```

### Windows PowerShell

```powershell
Get-Content .env | Where-Object { $_ -match '^\s*[^#][^=]*=' } | ForEach-Object {
  $name, $value = $_ -split '=', 2
  [Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim())
}
```

### Windows CMD

```bat
for /f "usebackq tokens=1,* delims==" %A in (".env") do @if not "%A"=="" if not "%A:~0,1%"=="#" set "%A=%B"
```

`.env` 已被 Git 忽略；只在本机填写真实签名凭据，禁止提交。Release 签名变量包括 `FRAME_FORWARD_RELEASE_STORE_FILE`、`FRAME_FORWARD_RELEASE_STORE_PASSWORD`、`FRAME_FORWARD_RELEASE_KEY_ALIAS` 和 `FRAME_FORWARD_RELEASE_KEY_PASSWORD`。

## 安装与运行

在本目录执行：

```sh
npm install

# 启动 Metro 开发服务器
npm start
```

另开一个终端后，构建并安装 Android 应用：

```sh
npm run android
```

也可用以下命令仅校验 Android Debug 构建：

```sh
npm run android:check
```

若要准备并运行 iOS：

```sh
npm run ios:prepare
npm run ios
```

## 质量检查

```sh
# 格式、Lint、类型检查和 Jest 测试
npm run quality

# 分项执行
npm run format:check
npm run lint
npm run typecheck
npm test
```

`npm run format` 和 `npm run lint:fix` 会改写文件，仅在需要显式修复格式或可自动修复的 Lint 问题时使用。

## 目录说明

| 路径              | 职责                                       |
| ----------------- | ------------------------------------------ |
| `src/app/`        | 应用启动、组合根、路由和会话装配           |
| `src/features/`   | 按摄影业务能力划分的功能模块及公共入口     |
| `src/services/`   | 跨功能复用的网络、存储、安全和原生适配能力 |
| `src/components/` | 跨功能复用的 UI 能力                       |
| `android/`        | Android 原生工程                           |
| `ios/`            | iOS 原生工程                               |
| `docs/`           | 客户端发布检查说明                         |

功能模块遵循 Presentation、Application、Domain、Infrastructure 的边界；页面和业务逻辑不应直接访问网络、存储或原生 SDK。

## 相关文档

- [根目录项目说明](../README.md)
- [产品需求文档](../docs/product/prd/versions/frame-forward-prd-v1.0.0-approved.md)
- [客户端发布检查](docs/release-gates.md)
- [APP 工程开发规范](../docs/constitution/app-development-constitution.md)
