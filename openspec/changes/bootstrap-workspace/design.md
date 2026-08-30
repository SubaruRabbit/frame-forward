## Context

当前仓库没有可构建代码。参见 `proposal.md`；本change只建立后续changes共同依赖的工程边界。

## Goals / Non-Goals

**Goals:**

- 三个项目目录可独立验证。
- 服务端以单一Spring Boot进程装配模块。
- OpenAPI文件具有独立校验入口。

**Non-Goals:**

- 不选择业务状态库、UI体系或AI工作流实现细节。
- 不加入部署目录。

## Decisions

- 根目录只做项目聚合，不创建跨App与服务端的混合构建。这样保持独立发布边界；不采用单一语言monorepo工具。
- 服务端建立 `bootstrap`、`common`、`modules` Maven骨架。业务模块在各自change中增加，避免空模块泛滥。
- `contracts` 独立保存OpenAPI并提供校验脚本，生成客户端留给接口相关change。

受影响区域：仓库根目录、`frame-forward-app`、`frame-forward-server`、`contracts`、`scripts`。

## Risks / Trade-offs

- [初始版本选择可能快速过时] → 只锁定满足PRD的稳定基线，依赖升级另开change。
- [空骨架过度设计] → 只创建能被构建或校验的最少文件。
