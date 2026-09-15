## Context

见 `proposal.md`。器材域包含三张目录表、`user_equipment` 表和四个 Entity。

## Goals / Non-Goals

**Goals:** 完整描述四张表及全部列，并为四个 Entity 的全部字段增加逐字段中文 Javadoc。

**Non-Goals:** 不修改目录种子、生成列表达式、唯一约束、类型或业务规则。

## Decisions

1. 新增 `V20__document_equipment_schema.sql`，保留最终列定义，为 `catalog_cameras`、`catalog_lenses`、`catalog_accessory_types`、`user_equipment` 写入中文 COMMENT。
2. `CameraEntity`、`LensEntity`、`AccessoryTypeEntity`、`UserEquipmentEntity` 字段各占一行并紧邻中文 Javadoc。
3. 先增加失败测试，使用 `information_schema` 校验四表全列注释，并静态校验四个 Entity 的字段布局与中文 Javadoc。

## Risks / Trade-offs

- [Risk] 修改生成列时破坏 `primary_camera_account_id` 表达式 → 完整保留表达式并加入列元数据断言。
- [Risk] 紧凑多字段声明拆分后漏字段 → 测试使用明确字段清单逐项验证。

## Migration Plan

随 Flyway 向前应用 V20；回退通过后续迁移清空 COMMENT。
