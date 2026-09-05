# 下一张 FrameForward

> Make your next shot better.

下一张 FrameForward 是一款面向微单摄影学习和实拍指导的 AI 摄影教练。它把学习、现场分析、拍摄方案、作品点评和重拍比较串成闭环，帮助用户发现当前照片的问题，并拍好下一张。

当前版本以中国大陆 Android 用户体验为目标，客户端使用 React Native 和 TypeScript，服务端使用 Java 21 与 Spring Boot。

## 项目结构

| 目录                                             | 说明                                |
| ------------------------------------------------ | ----------------------------------- |
| [`frame-forward-app/`](frame-forward-app/)       | React Native 客户端（Android 优先） |
| [`frame-forward-server/`](frame-forward-server/) | Spring Boot 多模块服务端            |
| [`contracts/`](contracts/)                       | OpenAPI 契约、兼容性检查与测试      |
| [`docs/`](docs/)                                 | 产品、架构与工程文档                |
| [`openspec/`](openspec/)                         | 变更提案、设计和规格                |
| [`scripts/`](scripts/)                           | 跨项目质量检查脚本                  |

产品范围和需求基线见[已批准的 PRD](docs/product/prd/versions/frame-forward-prd-v1.0.0-approved.md)，其余文档见[文档索引](docs/README.md)。

## 环境要求

- Node.js 22.11 或更高版本
- npm
- JDK 21
- Maven 3.9 或更高版本
- Android 开发环境：Android Studio、Android SDK 及已配置的模拟器或设备

首次安装各 JavaScript 工作区的依赖：

```sh
npm --prefix frame-forward-app install
npm --prefix contracts install
```

## 本地环境变量

根目录提供[`.env.example`](.env.example)作为可提交的配置模板；首次使用时复制为 `.env`，再按本机环境填写：

```sh
cp .env.example .env
# 编辑 .env 后导出为当前 shell 的进程环境变量
set -a && . ./.env && set +a
```

`.env` 已被 Git 忽略，不得提交真实密码、密钥或签名凭据。Spring Boot 和 Android Gradle 读取的是进程环境变量，并不会自动解析 dotenv 文件；启动服务端或执行 Android Release 构建前，请先运行上述导出命令。

模板包含服务端数据源、媒体目录、AI 模型路由与 Android Release 签名变量。各变量的用途和示例见[`.env.example`](.env.example)。

## 常用命令

在仓库根目录运行：

```sh
# 启动 Android 客户端
npm run android

# 执行全部质量门禁：客户端、服务端和 API 契约
npm run quality

# 按模块执行质量门禁
npm run quality:app
npm run quality:server
npm run quality:contracts
```

客户端的更多运行、构建和 iOS 准备命令见[`frame-forward-app/package.json`](frame-forward-app/package.json)。全部工作区检查也可在 Linux/macOS 上通过 `./scripts/check.sh` 运行；Windows 请使用 `./scripts/check.ps1`。详情见[质量检查说明](scripts/README.md)。

## 工程约定

- 开始变更前，请阅读仓库根目录的[AGENTS.md](AGENTS.md)和[项目宪法](constitution.md)。
- 接口变更应同步更新[`contracts/openapi.yaml`](contracts/openapi.yaml)及相应测试。
- 新功能或需求范围变更应遵循[`openspec/`](openspec/)中的规格与任务流程。
