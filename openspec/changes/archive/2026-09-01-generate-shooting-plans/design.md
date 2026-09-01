## Context
方案以已完成scene和器材快照为输入，不能重新解释器材所有权。
## Goals / Non-Goals
**Goals:** 2～3个完整、兼容、安全且有排序的方案。
**Non-Goals:** 参考图和作品评价。
## Decisions
- `shooting` 模块为plan定义固定schema；`qwen3.7-plus`只填充schema字段。
- 生成后依次执行卡口/画幅、相机能力、附件所有权、曝光范围和位置安全规则；不合格方案修复一次后丢弃。
- 保留scene、器材快照、提示词与规则版本；App默认展开recommended plan并允许切换。
## Risks / Trade-offs
- [规则过滤后少于2个方案] → 任务失败并提供可重试错误，不用残缺方案凑数。
