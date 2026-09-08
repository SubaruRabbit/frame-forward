package com.frameforward.evaluation.repository;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.frameforward.evaluation.manager.EvaluationManager;
import com.frameforward.evaluation.mapper.*;
import com.frameforward.evaluation.model.entity.*;
import com.frameforward.evaluation.service.EvaluationWorkCleanup;
class EvaluationRepositoryTest {
    @Test
    void cacheLatestAndCandidatesKeepVersionOwnershipAndCompletionFilters() {
        initialize();
        var evaluations = mock(PhotoEvaluationMapper.class);
        var repository = new EvaluationRepository(evaluations, mock(ShootingSessionMapper.class),
                mock(RetakeLinkMapper.class));
        when(evaluations.selectOne(any())).thenAnswer(call -> {
            LambdaQueryWrapper<?> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("account_id", "content_hash", "rule_version",
                    "execution_version", "session_id IS NULL");
            assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("owner", "hash", "v1", "v1");
            return null;
        });
        repository.findCached("owner", "hash", " ");
        doAnswer(call -> {
            LambdaQueryWrapper<?> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("account_id", "media_id", "ORDER BY created_at DESC", "LIMIT 1");
            assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("owner", "media");
            return null;
        }).when(evaluations).selectOne(any());
        repository.latestOwned("owner", "media");
        when(evaluations.selectList(any())).thenAnswer(call -> {
            LambdaQueryWrapper<?> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("account_id", "session_id", "result_json IS NOT NULL");
            assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("owner", "session");
            return List.of();
        });
        assertThat(repository.completedCandidates("owner", "session")).isEmpty();
    }
    @Test
    void cleanupKeepsOwnedWorkSelectionAndLinkDeletionBeforeEvaluations() {
        initialize();
        var evaluations = mock(PhotoEvaluationMapper.class);
        var links = mock(RetakeLinkMapper.class);
        var cleanup = new EvaluationWorkCleanup(
                new EvaluationManager(new EvaluationRepository(evaluations, mock(ShootingSessionMapper.class), links)));
        var owned = new PhotoEvaluationEntity();
        owned.id = "eval";
        when(evaluations.selectList(any())).thenAnswer(call -> {
            assertOwnedWork(call.getArgument(0));
            return List.of(owned);
        });
        when(links.delete(any())).thenAnswer(call -> {
            LambdaQueryWrapper<?> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("original_evaluation_id", "OR", "retake_evaluation_id");
            assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("eval", "eval");
            return 1;
        });
        when(evaluations.delete(any())).thenAnswer(call -> {
            assertOwnedWork(call.getArgument(0));
            return 1;
        });
        cleanup.deleteForWork("owner", "media");
        var order = inOrder(evaluations, links);
        order.verify(evaluations).selectList(any());
        order.verify(links).delete(any());
        order.verify(evaluations).delete(any());
        var failure = new IllegalStateException("failed");
        doThrow(failure).when(links).delete(any());
        assertThatThrownBy(() -> cleanup.deleteForWork("owner", "media")).isSameAs(failure);
        verify(evaluations, times(1)).delete(any());
    }
    private static void assertOwnedWork(LambdaQueryWrapper<?> query) {
        assertThat(query.getSqlSegment()).contains("account_id", "media_id");
        assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("owner", "media");
    }
    private static void initialize() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "evaluations"),
                PhotoEvaluationEntity.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "links"),
                RetakeLinkEntity.class);
    }
}
