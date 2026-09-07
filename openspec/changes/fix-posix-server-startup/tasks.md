## 1. POSIX 启动流程

- [x] 1.1 `scripts/start-server.sh`：先构建并安装 `bootstrap` 的 reactor 依赖，再单独运行 `bootstrap`；通过检查第二次 Maven 调用不含 `-am` 验证运行目标范围。
- [x] 1.2 `scripts/start-server.sh`：保持构建失败时不启动应用的语义；通过 shell 语法检查和构建命令失败时的退出码验证。

## 2. 开发者说明与验证

- [x] 2.1 `frame-forward-server/README.md`：将手动启动示例更新为两阶段命令；通过与 POSIX 脚本和 PowerShell 脚本的 Maven 参数比对验证一致性。
- [x] 2.2 `frame-forward-server`：执行 Spotless 及 `QUALITY_BASE_REF=HEAD mvn -q -DskipTests=false verify`，并运行启动构建阶段，验证主类错误不再发生。
