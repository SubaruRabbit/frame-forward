package com.frameforward.generation.repository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.frameforward.generation.mapper.ReferenceImageMapper;
import com.frameforward.generation.model.entity.ReferenceImageEntity;
import com.frameforward.shooting.mapper.ShootingPlanMapper;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;
class ReferenceImageRepositoryTest {
    @Test
    void filtersPlansByOwnerAndReferencesByTask() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "plans"),
                ShootingPlanEntity.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "references"),
                ReferenceImageEntity.class);
        var plans = mock(ShootingPlanMapper.class);
        var references = mock(ReferenceImageMapper.class);
        var repository = new ReferenceImageRepository(plans, references);
        when(plans.selectOne(any())).thenAnswer(call -> {
            LambdaQueryWrapper<?> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("id", "account_id");
            assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("plan", "owner");
            return null;
        });
        when(references.selectOne(any())).thenAnswer(call -> {
            LambdaQueryWrapper<?> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("ai_task_id");
            assertThat(query.getParamNameValuePairs().values()).containsExactly("task");
            return null;
        });
        assertThat(repository.findOwnedPlan("owner", "plan")).isNull();
        assertThat(repository.findByTask("task")).isNull();
    }
}
