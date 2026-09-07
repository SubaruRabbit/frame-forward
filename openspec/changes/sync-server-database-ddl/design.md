## 目标与边界

仅在 `frame-forward-server/bootstrap` 增加一个版本化 Flyway 迁移，修正 `lesson_progress` 和 `lesson_assignment_feedback` 的账户列定义，并补充账户外键。课程内容版本表不涉及账户归属，仅统一表级 MySQL 定义。

## 迁移设计

新增 `V17__align_course_delivery_account_schema.sql`：

1. 先移除 V7 创建的 `fk_progress_course_version`，避免父表转换时因现有父子列排序规则不一致而触发 MySQL 3780。
2. 将 `course_content_versions` 及两张交付表的关联列统一为 `utf8mb4` 与 `utf8mb4_0900_ai_ci`，将两张交付表的 `account_id` 修改为 `CHAR(36)`。
3. 两张交付表和 `course_content_versions` 统一声明 `ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci`，然后恢复 `fk_progress_course_version`。
4. 为两张交付表的 `account_id` 增加指向 `accounts(id)` 的 `ON DELETE CASCADE` 外键。账户删除流程先执行显式清理，因此该级联是兜底保护，不改变现有业务行为。

迁移只包含结构兼容的 `ALTER TABLE`，不删除或重命名列。由于目标列现有值均为账户标识，类型调整不会改变业务数据语义；若历史数据库存在不合法账户值，迁移应由外键添加失败明确阻断。

## 回滚

Flyway 迁移不执行自动回滚。若升级失败，数据库保持在 V16，修复数据后重新执行 V17；若需人工回退，按相反顺序移除新增外键并恢复原列类型/表级定义，先确认无超出 `VARCHAR(36)` 的值。应用代码无需回滚。

## 验证

- 运行课程/账户删除相关测试。
- 使用 Flyway 对空 MySQL 8.4 数据库执行全部迁移。
- 使用测试数据库检查两张课程表的列类型、排序规则、外键和删除行为。
