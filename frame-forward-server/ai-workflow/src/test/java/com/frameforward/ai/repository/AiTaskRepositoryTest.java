package com.frameforward.ai.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.frameforward.ai.mapper.AiTaskMapper;
import com.frameforward.ai.model.entity.AiTaskEntity;

class AiTaskRepositoryTest {
    @Test
    void preservesRunningAndIdempotencyQueryConditions() {
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "ai-test"), AiTaskEntity.class);
        var mapper = mock(AiTaskMapper.class);
        var task = new AiTaskEntity();
        when(mapper.selectList(any())).thenAnswer(call -> {
            LambdaQueryWrapper<AiTaskEntity> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("state =");
            assertThat(query.getParamNameValuePairs().values()).containsExactly("RUNNING");
            return List.of(task);
        });
        when(mapper.selectOne(any())).thenAnswer(call -> {
            LambdaQueryWrapper<AiTaskEntity> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("account_id =", "operation_type =", "idempotency_key =");
            assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("owner", "coach", "key");
            return task;
        });
        var repository = new AiTaskRepository(mapper);
        assertThat(repository.findRunning()).containsExactly(task);
        assertThat(repository.findExisting("owner", "coach", "key")).isSameAs(task);
    }

    @Test
    void delegatesPrimaryKeyAndWritesWithoutChangingEntity() {
        var mapper = mock(AiTaskMapper.class);
        var task = new AiTaskEntity();
        task.id = "task";
        when(mapper.selectById("task")).thenReturn(task);
        var repository = new AiTaskRepository(mapper);
        assertThat(repository.findById("task")).isSameAs(task);
        repository.create(task);
        repository.update(task);
        verify(mapper).insert(task);
        verify(mapper).updateById(task);
        assertThat(task.updatedAt).isNull();
    }
}
