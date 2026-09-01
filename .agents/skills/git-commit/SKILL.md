---
name: git-commit
description: 触发当前项目的 git-commit subagent，检查并安全提交用户明确要求的代码变更。
---

# Git 提交触发器

当用户明确要求提交代码，或显式调用本 skill 时，触发项目级 `git-commit` subagent 执行提交流程。

## 执行要求

1. 使用项目 `.codex/agents/git-commit.toml` 中注册的 `git-commit` subagent；模型必须固定为 `gpt-5.6-luna`。
2. 将用户当前任务、提交范围、期望提交信息（如有）和仓库路径完整传给 subagent。
3. 在 subagent 返回前不要自行执行 `git add`、`git commit` 或远端推送，避免绕过其检查流程。
4. 若 subagent 发现变更范围不明确、测试失败、敏感信息或其他提交风险，停止并将问题原样反馈给用户。
5. 提交成功后向用户汇报提交哈希、提交信息、涉及文件和验证结果；本 skill 不触发远端 push。

## 授权边界

- 只有用户明确要求提交时，才允许 subagent 创建本地 Git commit。
- 禁止强制推送、重写历史、删除文件、`git reset --hard`、`git checkout --`、`git clean -fd` 和 `git rebase`。
- 不得覆盖或回退其他用户或代理已有的修改。

## 委派提示

如果当前运行环境支持协作 subagent 工具，使用 `git-commit` 作为目标角色并指定模型 `gpt-5.6-luna`；否则读取 `.codex/agents/git-commit.toml` 的 instructions，按同样边界执行。
