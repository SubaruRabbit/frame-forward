# FrameForward 服务端

FrameForward 服务端为“下一张 FrameForward”提供认证、器材、媒体、课程、AI 工作流、拍摄、生成、点评和作品集等后端能力。项目基于 Java 21、Spring Boot 3.5 和 Maven 构建，`bootstrap` 模块负责装配并启动应用。

## 模块说明

| 模块 | 职责 |
| --- | --- |
| `common` | 跨模块共享的基础能力 |
| `auth` | 认证与账号相关能力 |
| `equipment` | 摄影器材目录与用户器材 |
| `media` | 图片媒体管理 |
| `course` | 摄影课程能力 |
| `ai-workflow` | AI 任务编排与状态推送 |
| `shooting` | 场景分析与拍摄方案 |
| `generation` | 参考图片生成 |
| `evaluation` | 照片点评与重拍比较 |
| `portfolio` | 作品集浏览与管理 |
| `bootstrap` | Spring Boot 启动模块与模块装配 |

## 环境要求

- JDK 21
- Maven 3.9 或更高版本
- MySQL（本地开发环境）

本地开发使用 `dev` Profile。根目录的[`.env.example`](../.env.example)提供全部环境变量模板；首次使用时，在仓库根目录复制并填写本地配置：

```sh
cp .env.example .env
```

`.env` 已被 Git 忽略。服务端不会自动解析 dotenv 文件；运行前需将其导出为进程环境变量。至少填写 `FRAME_FORWARD_DB_URL`、`FRAME_FORWARD_DB_USERNAME` 和 `FRAME_FORWARD_DB_PASSWORD`；媒体目录和 AI 模型路由可按需调整。不得将真实密码或其他凭据提交到仓库。

在仓库根目录，根据当前操作系统和终端导出 `.env` 到当前进程：

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

## 启动应用

执行上面的环境变量导出命令后，再进入本目录启动：

```sh
cd frame-forward-server
mvn -pl bootstrap -am package install:install -DskipTests -Dspring-boot.repackage.skip=true
mvn -pl bootstrap spring-boot:run -Dspring-boot.run.profiles=dev
```

也可以在仓库根目录直接使用启动脚本，脚本会自动读取 `.env` 并导出变量：

```sh
# Linux / macOS
sh ./scripts/start-server.sh
```

```powershell
# Windows PowerShell
.\scripts\start-server.ps1
```

```bat
:: Windows CMD
scripts\start-server.cmd
```

应用入口为 `com.frameforward.bootstrap.FrameForwardApplication`，会扫描 `com.frameforward` 下的组件并执行 Flyway 数据库迁移。

## 质量检查

在本目录执行完整质量门禁：

```sh
QUALITY_BASE_REF=HEAD mvn -q -DskipTests=false verify
```

该命令会执行 Spotless 格式检查、测试、JaCoCo 报告、Checkstyle 以及 PMD/CPD 检查。`QUALITY_BASE_REF` 必须是可解析的 Git 提交或引用；在 CI 或评审中，应设置为与目标分支比较的基线。

父 POM 统一配置以下 Maven 插件：

| 插件              | 用途                                                             | 执行方式                       |
| ----------------- | ---------------------------------------------------------------- | ------------------------------ |
| Spotless          | 按 Eclipse JDT、导入顺序和 POM 排序规则统一格式                  | `validate` 阶段自动检查        |
| OpenRewrite       | 使用 `NeedBraces` recipe 为条件和循环语句补全大括号              | 手动执行 `mvn rewrite:run`     |
| Maven Checkstyle  | 检查生产代码和测试代码中的条件、循环语句是否使用大括号           | `verify` 阶段自动检查          |
| JaCoCo            | 采集测试覆盖率并在各模块的 `target/site/jacoco/` 下生成 XML 报告 | `verify` 阶段自动生成          |
| Maven PMD         | 按仓库规则检查圈复杂度，并生成 PMD 与 CPD 重复代码报告           | `verify` 阶段自动检查/生成报告 |
| Flyway Maven 插件 | 提供数据库迁移相关 Maven goal；执行时需先配置数据库连接环境变量  | 按需手动执行                   |

如需在提交 Java 代码前修复格式并应用大括号规则：

```sh
mvn spotless:apply rewrite:run
```

## 相关文档

- [根目录项目说明](../README.md)
- [OpenAPI 契约](../contracts/openapi.yaml)
- [产品需求文档](../docs/product/prd/versions/frame-forward-prd-v1.0.0-approved.md)
- [Java 后端工程宪章](../docs/constitution/java-backend-constitution.md)
