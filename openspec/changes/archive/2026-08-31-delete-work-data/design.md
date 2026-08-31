## Context
单作品关联portfolio记录、media文件和evaluation结果；删除必须真实完成。
## Goals / Non-Goals
**Goals:** 小范围作品清理和失败续做。
**Non-Goals:** 账号注销与批量清空。
## Decisions
- `portfolio` 模块拥有删除用例并调用 `evaluation` 与 `media` 的公开清理端口，不直接操作其表。
- 删除作业记录每个清理步骤；产品读取入口先拒绝访问，全部步骤成功后才标记完成。
- 文件删除设计为幂等，重试已完成步骤不会报错。
## Risks / Trade-offs
- [部分失败形成孤儿文件] → 删除作业保留失败步骤并在本地运行时重试。
