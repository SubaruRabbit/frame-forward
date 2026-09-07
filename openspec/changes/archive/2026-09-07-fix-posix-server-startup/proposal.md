## Why

POSIX 启动脚本将 `-am` 与 `spring-boot:run` 放在同一次 Maven 调用中，导致父聚合工程也执行运行目标并因缺少主类而失败。本地开发者无法通过仓库提供的 macOS/Linux 入口启动服务。

## What Changes

- 将 POSIX 启动流程改为先构建并安装 bootstrap 所需 reactor 模块，再仅对 `bootstrap` 执行 Spring Boot 运行目标。
- 统一服务端 README 的手动启动命令与脚本的两阶段流程。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。

## Impact

影响 `scripts/start-server.sh` 与 `frame-forward-server/README.md` 的本地开发工具说明；不改变服务 API、业务行为、数据库或 Windows 启动流程。

## Dependencies

- Maven reactor、`bootstrap` 启动模块和开发环境 `.env` 配置。

## Non-goals

- 不修改 Maven 模块结构、Spring Boot 应用入口或生产部署流程。
