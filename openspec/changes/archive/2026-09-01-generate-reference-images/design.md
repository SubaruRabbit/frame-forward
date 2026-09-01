## Context
参考图依赖已选方案和现场图，失败不能影响文字方案。
## Goals / Non-Goals
**Goals:** 可追溯生成、参考声明和独立失败。
**Non-Goals:** 通用图片编辑与评分。
## Decisions
- `generation` 模块从plan schema生成受控提示，调用 `qwen-image-3.0-pro`，结果作为独立media资源关联plan。
- 工作流只接收当前用户拥有的scene与plan；输出页面固定展示三项参考边界。
- 失败状态只属于generation task，plan生命周期不随之改变。
## Risks / Trade-offs
- [参考图偏离现场] → 提示保留空间结构、光线和视角，结果仍明确标为参考。
