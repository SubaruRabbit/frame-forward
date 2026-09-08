package com.frameforward.shooting;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
class ShootingPlanTypesMigrationTest {
    @Test
    void canonicalEntityKeepsTableColumnsAndMapperGenericType() throws Exception {
        var entity = Class.forName("com.frameforward.shooting.model.entity.ShootingPlanEntity");
        var mapper = Class.forName("com.frameforward.shooting.mapper.ShootingPlanMapper");
        var table = TableInfoHelper
                .initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "plan-migration"), entity);
        assertThat(table.getTableName()).isEqualTo("shooting_plans");
        assertThat(table.getKeyColumn()).isEqualTo("id");
        assertThat(table.getFieldList()).extracting("column").containsExactlyInAnyOrder("account_id",
                "scene_analysis_id", "ai_task_id", "scene_snapshot_json", "equipment_snapshot_json", "created_at");
        assertThat(ResolvableType.forClass(mapper).as(BaseMapper.class).getGeneric(0).resolve()).isEqualTo(entity);
        var data = entity.getConstructor().newInstance();
        entity.getField("id").set(data, "plan");
        entity.getField("accountId").set(data, "owner");
        entity.getField("aiTaskId").set(data, "task");
        assertThat(entity.getMethod("getId").invoke(data)).isEqualTo("plan");
        assertThat(entity.getMethod("getAccountId").invoke(data)).isEqualTo("owner");
        assertThat(entity.getMethod("getAiTaskId").invoke(data)).isEqualTo("task");
    }
}
