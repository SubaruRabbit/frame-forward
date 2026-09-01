## Context
首版无CMS，但P0课程必须在验收前可用并能追溯生成依据。
## Goals / Non-Goals
**Goals:** 缓存课程、不可变版本、事实约束、进度和作业点评。
**Non-Goals:** 视频、后台编辑和连接控制。
## Decisions
- `course` 模块保存course、contentVersion、lesson、progress；P0内容由本地生成命令调用 `qwen3.7-plus` 后写入草稿，再经自动schema与术语校验进入可用状态。
- 机型教程检索版本化器材资料；无法证实的固件菜单写入固定免责声明。
- 用户进度绑定contentVersion；作业复用media ingestion并启动聚焦当前lesson目标的AI task。
## Risks / Trade-offs
- [无后台导致修正不便] → 内容有误反馈记录课程版本，重新生成产生新版本。
- [范围过大] → 本change只交付P0课程结构和一条通用生成管线，不做全部扩展题材。
