package com.frameforward.portfolio.business;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.frameforward.evaluation.gateway.WorkEvaluationCleanup;
import com.frameforward.media.gateway.WorkMediaCleanup;
import com.frameforward.portfolio.manager.PortfolioManager;
import com.frameforward.portfolio.mapper.PortfolioFavoriteMapper;
import com.frameforward.portfolio.mapper.PortfolioWorkDeletionJobMapper;
import com.frameforward.portfolio.model.dto.DeletionJob;
import com.frameforward.portfolio.model.dto.Favorite;
import com.frameforward.portfolio.model.entity.PortfolioFavoriteEntity;
import com.frameforward.portfolio.model.entity.PortfolioWorkDeletionJobEntity;

class PortfolioBusinessTest {
    private final PortfolioFavoriteMapper favorites = mock(PortfolioFavoriteMapper.class);
    private final PortfolioWorkDeletionJobMapper deletionJobs = mock(PortfolioWorkDeletionJobMapper.class);
    private final WorkEvaluationCleanup evaluationCleanup = mock(WorkEvaluationCleanup.class);
    private final WorkMediaCleanup mediaCleanup = mock(WorkMediaCleanup.class);
    private final PortfolioBusiness business = new PortfolioBusiness(
            new PortfolioManager(new com.frameforward.portfolio.repository.PortfolioRepository(favorites, deletionJobs),
                    evaluationCleanup, mediaCleanup));

    @Test
    void createsFavoriteOnlyWhenItDoesNotAlreadyExist() {
        when(favorites.selectOne(any())).thenReturn(null);

        Favorite favorite = business.setFavorite("account-1", "media-1", true, true);

        assertEquals(new Favorite("media-1", true), favorite);
        verify(favorites).insert(any(PortfolioFavoriteEntity.class));
    }

    @Test
    void deletesWorkAndAssociatedData() {
        when(deletionJobs.selectOne(any())).thenReturn(null);

        DeletionJob job = business.delete("account-1", "media-1", true);

        assertEquals("COMPLETED", job.state());
        verify(evaluationCleanup).deleteForWork("account-1", "media-1");
        verify(mediaCleanup).deleteForWork("account-1", "media-1");
        verify(favorites).delete(any());
        verify(deletionJobs, times(2)).updateById(any(PortfolioWorkDeletionJobEntity.class));
    }

    @Test
    void keepsFailedDeletionRetryable() {
        PortfolioWorkDeletionJobEntity existing = new PortfolioWorkDeletionJobEntity();
        existing.id = "job-1";
        existing.accountId = "account-1";
        existing.mediaId = "media-1";
        existing.state = "FAILED";
        when(deletionJobs.selectOne(any())).thenReturn(existing);
        org.mockito.Mockito.doThrow(new IllegalStateException("cleanup failed")).when(evaluationCleanup)
                .deleteForWork(eq("account-1"), eq("media-1"));

        DeletionJob job = business.delete("account-1", "media-1", true);

        assertEquals("FAILED", job.state());
        assertEquals("关联数据清理失败，请重试。", job.failureReason());
    }
}
