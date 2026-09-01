## Context
依赖器材目录，用户只能引用目录项建立私有器材库。
## Goals / Non-Goals
**Goals:** 器材CRUD、唯一主力相机和兼容提示。
**Non-Goals:** 自定义目录项、转接环和设备连接。
## Decisions
- `equipment` 模块保存用户器材引用、昵称、常用状态；主力相机切换在同一事务中完成并由唯一约束保护。
- 兼容服务复用目录规则，创建拍摄上下文时返回可执行组合而非静默接受不兼容组合。
- `frame-forward-app/features/equipment` 分离目录搜索与“我的器材”，仅feature内部持有编辑状态。
## Risks / Trade-offs
- [用户拥有未收录型号] → P0只允许目录选择，扩充目录另开change。
