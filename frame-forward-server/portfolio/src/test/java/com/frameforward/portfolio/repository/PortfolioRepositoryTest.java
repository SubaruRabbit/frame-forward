package com.frameforward.portfolio.repository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.frameforward.portfolio.mapper.*;
import com.frameforward.portfolio.model.entity.*;
class PortfolioRepositoryTest {
    @Test
    void queriesAndFavoriteDeletionRemainOwnerScoped() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "favorites"),
                PortfolioFavoriteEntity.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "jobs"),
                PortfolioWorkDeletionJobEntity.class);
        var favorites = mock(PortfolioFavoriteMapper.class);
        var jobs = mock(PortfolioWorkDeletionJobMapper.class);
        var repository = new PortfolioRepository(favorites, jobs);
        when(favorites.selectOne(any())).thenAnswer(call -> {
            assertScoped(call.getArgument(0), "media_id", "media");
            return null;
        });
        when(favorites.delete(any())).thenAnswer(call -> {
            assertScoped(call.getArgument(0), "media_id", "media");
            return 1;
        });
        when(jobs.selectOne(any())).thenAnswer(call -> {
            assertScoped(call.getArgument(0), "media_id", "media");
            return null;
        });
        assertThat(repository.findFavorite("owner", "media")).isNull();
        repository.deleteFavorite("owner", "media");
        assertThat(repository.findDeletionJob("owner", "media")).isNull();
        doAnswer(call -> {
            assertScoped(call.getArgument(0), "id", "job");
            return null;
        }).when(jobs).selectOne(any());
        assertThat(repository.findDeletionJobById("owner", "job")).isNull();
    }
    private static void assertScoped(LambdaQueryWrapper<?> query, String column, String value) {
        assertThat(query.getSqlSegment()).contains("account_id", column);
        assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("owner", value);
    }
}
