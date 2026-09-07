## Context

见 `proposal.md`。`bootstrap` 依赖同一 reactor 中的业务模块；`-am` 能构建它们，却也会使命令行指定的 `spring-boot:run` 对每个选中的项目执行。现有 PowerShell 脚本已采用“构建并安装，再运行”的流程，POSIX 脚本和 README 尚未同步。

## Goals / Non-Goals

**Goals:** 让 macOS/Linux 脚本与手动 Maven 命令只在 `bootstrap` 模块执行 Spring Boot 启动，同时确保其 reactor 依赖在本地 Maven 仓库可解析。

**Non-Goals:** 不改变 Maven POM、应用主类、运行 Profile、环境变量加载或 Windows 脚本。

## Decisions

- 先以 `-pl bootstrap -am` 构建并安装依赖模块，禁用仅对启动无关的 Spring Boot repackage；再以 `-pl bootstrap` 执行 `spring-boot:run`。这与现有 PowerShell 脚本一致，并隔离运行目标到唯一拥有主类的模块。
- README 展示相同的两条 Maven 命令，避免用户复制会触发父 POM 运行目标的旧命令。备选方案是在父 POM 声明主类不可行，因为聚合模块不是可运行应用，也会掩盖命令作用域错误。

## Risks / Trade-offs

- [首次启动需要先安装 reactor 产物] → 脚本和文档明确两阶段命令；构建失败时不进入启动阶段。
- [构建阶段耗时增加] → 仅构建 bootstrap 所需模块，且沿用 Windows 已验证流程。

## Migration Plan

更新 POSIX 脚本和说明后，运行构建阶段并验证第二阶段 Maven reactor 只包含 `bootstrap`，随后可由开发者以本地 `.env` 和 MySQL 配置实际启动。回滚仅恢复原脚本与文档；不会影响已安装依赖或数据库。
