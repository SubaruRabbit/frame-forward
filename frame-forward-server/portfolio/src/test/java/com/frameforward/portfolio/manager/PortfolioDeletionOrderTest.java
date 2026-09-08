package com.frameforward.portfolio.manager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.frameforward.evaluation.gateway.WorkEvaluationCleanup;
import com.frameforward.media.gateway.WorkMediaCleanup;
import com.frameforward.portfolio.model.entity.PortfolioWorkDeletionJobEntity;
import com.frameforward.portfolio.repository.PortfolioRepository;
class PortfolioDeletionOrderTest {
    @Test
    void preservesFailureStateAndRetriesCleanupInOriginalOrder() {
        var repository = mock(PortfolioRepository.class);
        var evaluation = mock(WorkEvaluationCleanup.class);
        var media = mock(WorkMediaCleanup.class);
        var manager = new PortfolioManager(repository, evaluation, media);
        var job = new PortfolioWorkDeletionJobEntity();
        job.id = "job";
        job.accountId = "owner";
        job.mediaId = "media";
        var states = new ArrayList<String>();
        doAnswer(call -> {
            states.add(job.state);
            return null;
        }).when(repository).updateDeletionJob(job);
        doThrow(new IllegalStateException("private storage detail")).doNothing().when(evaluation).deleteForWork("owner",
                "media");
        manager.processDeletion(job);
        assertThat(job.state).isEqualTo("FAILED");
        assertThat(job.failureReason).isEqualTo("关联数据清理失败，请重试。");
        verify(repository, never()).deleteFavorite(any(), any());
        verifyNoInteractions(media);
        manager.processDeletion(job);
        assertThat(states).containsExactly("IN_PROGRESS", "FAILED", "IN_PROGRESS", "COMPLETED");
        assertThat(job.failureReason).isNull();
        var order = inOrder(evaluation, repository, media);
        order.verify(repository).updateDeletionJob(job);
        order.verify(evaluation).deleteForWork("owner", "media");
        order.verify(repository, times(2)).updateDeletionJob(job);
        order.verify(evaluation).deleteForWork("owner", "media");
        order.verify(repository).deleteFavorite("owner", "media");
        order.verify(media).deleteForWork("owner", "media");
        order.verify(repository).updateDeletionJob(job);
    }
}
