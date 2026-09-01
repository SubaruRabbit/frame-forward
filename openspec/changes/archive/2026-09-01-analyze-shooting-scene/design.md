## Context
依赖已拥有的JPEG、用户器材上下文和AI任务运行时。
## Goals / Non-Goals
**Goals:** 结构化环境事实与安全候选机位。
**Non-Goals:** 拍摄参数方案和参考图。
## Decisions
- `shooting` 模块创建scene记录并引用media与器材快照，避免后续器材修改改变既有分析。
- Graph节点依次校验输入、调用 `qwen3.8-max`、验证结构、安全过滤并持久化结果。
- `scene-analysis` feature使用字段化表单和结构化结果卡片，不渲染原始模型文本。
## Risks / Trade-offs
- [单张照片看不到场外危险] → 输出明确提示用户复核现场，安全过滤只认可可见事实。
