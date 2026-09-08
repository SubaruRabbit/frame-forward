package com.frameforward.ai.model.entity;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.frameforward.ai.mapper.AiTaskMapper;

class AiTaskEntityMappingTest {
    @Test
    void preservesTaskTableColumnsPrimaryKeyAndMapperType() {
        var configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        var table = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "ai-mapping"),
                AiTaskEntity.class);
        assertThat(table.getTableName()).isEqualTo("ai_tasks");
        assertThat(table.getKeyColumn()).isEqualTo("id");
        assertThat(table.getFieldList()).extracting(field -> field.getColumn()).containsExactlyInAnyOrder("account_id",
                "operation_type", "idempotency_key", "state", "workflow_version", "model_id", "prompt_version",
                "rule_version", "schema_version", "input_json", "result_json", "error_code", "created_at",
                "updated_at");
        assertThat(ResolvableType.forClass(AiTaskMapper.class).as(BaseMapper.class).getGeneric(0).resolve())
                .isEqualTo(AiTaskEntity.class);
        var entity = new AiTaskEntity();
        entity.id = "task";
        entity.accountId = "account";
        entity.operationType = "coach";
        entity.idempotencyKey = "key";
        entity.state = "QUEUED";
        assertThat(entity.getId()).isEqualTo("task");
        assertThat(entity.getAccountId()).isEqualTo("account");
        assertThat(entity.getOperationType()).isEqualTo("coach");
        assertThat(entity.getIdempotencyKey()).isEqualTo("key");
        assertThat(entity.getState()).isEqualTo("QUEUED");
    }
}
