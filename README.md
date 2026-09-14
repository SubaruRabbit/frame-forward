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
```

编辑 `.env` 后，根据当前操作系统和终端，将其中的变量导出到当前进程：

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

在仓库根目录执行以下命令（交互式 CMD 使用单个 `%`；写入 `.bat` 文件时改为 `%%`）：

```bat
for /f "usebackq tokens=1,* delims==" %A in (".env") do @if not "%A"=="" if not "%A:~0,1%"=="#" set "%A=%B"
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

### 服务端 Maven 工具

[`frame-forward-server/pom.xml`](frame-forward-server/pom.xml)统一配置以下 Maven 插件：

| 插件              | 用途                                                              | 执行方式                       |
| ----------------- | ----------------------------------------------------------------- | ------------------------------ |
| Spotless          | 按仓库中的 Eclipse JDT、导入顺序和 POM 排序规则统一格式           | `validate` 阶段自动检查        |
| OpenRewrite       | 使用 `NeedBraces` recipe 为条件和循环语句补全大括号               | 手动执行 `mvn rewrite:run`     |
| Maven Checkstyle  | 检查生产代码和测试代码中的条件、循环语句是否使用大括号            | `verify` 阶段自动检查          |
| JaCoCo            | 采集测试覆盖率并在各模块的 `target/site/jacoco/` 下生成 XML 报告  | `verify` 阶段自动生成          |
| Maven PMD         | 按仓库规则检查圈复杂度，并生成 PMD 与 CPD 重复代码报告            | `verify` 阶段自动检查/生成报告 |
| Flyway Maven 插件 | 提供数据库迁移相关的 Maven goal；执行时需先配置数据库连接环境变量 | 按需手动执行                   |

在服务端目录中可先自动修复格式和大括号，再执行完整质量门禁：

```sh
cd frame-forward-server
mvn spotless:apply rewrite:run
QUALITY_BASE_REF=HEAD mvn verify
```

`QUALITY_BASE_REF` 必须是可解析的 Git 基线引用；质量脚本默认使用 `HEAD`，也可在运行 `npm run quality:server` 前显式指定其他基线。

## 工程约定

- 开始变更前，请阅读仓库根目录的[AGENTS.md](AGENTS.md)和[项目宪法](constitution.md)。
- 接口变更应同步更新[`contracts/openapi.yaml`](contracts/openapi.yaml)及相应测试。
- 新功能或需求范围变更应遵循[`openspec/`](openspec/)中的规格与任务流程。
