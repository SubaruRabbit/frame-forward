## Context
首版在本地文件系统持久化JPEG，PostgreSQL保存资源元数据。
## Goals / Non-Goals
**Goals:** 安全上传、EXIF解析、方向正确和GPS隔离。
**Non-Goals:** HEIF、RAW和对象存储接入。
## Decisions
- `media` 模块先流式写临时文件，完成大小、魔数和解码校验后再原子移动到不可预测路径。
- 原图不可变；生成方向校正且移除GPS的AI副本，摄影EXIF解析为白名单字段。
- 内容散列用于幂等识别，资源记录始终带owner；`photo-import` feature通过系统选择器和上传进度接口接入。
## Risks / Trade-offs
- [超大像素导致内存耗尽] → 解码设置像素和资源上限并拒绝异常文件。
- [文件与数据库不一致] → 临时文件清理和失败补偿记录分开处理。
