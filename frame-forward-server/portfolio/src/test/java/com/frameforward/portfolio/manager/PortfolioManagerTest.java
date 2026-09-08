package com.frameforward.portfolio.manager;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.frameforward.evaluation.gateway.WorkEvaluationCleanup;
import com.frameforward.media.gateway.WorkMediaCleanup;
import com.frameforward.portfolio.business.PortfolioBusiness;
import com.frameforward.portfolio.business.PortfolioNotFound;
import com.frameforward.portfolio.mapper.PortfolioFavoriteMapper;
import com.frameforward.portfolio.mapper.PortfolioWorkDeletionJobMapper;
import com.frameforward.portfolio.model.entity.PortfolioFavoriteEntity;
import com.frameforward.portfolio.model.entity.PortfolioWorkDeletionJobEntity;

class PortfolioManagerTest {
    @Test
    void managerHandlesFavoriteAndDeletionSuccessAndFailure() {
        var favorites = mock(PortfolioFavoriteMapper.class);
        var jobs = mock(PortfolioWorkDeletionJobMapper.class);
        var evaluations = mock(WorkEvaluationCleanup.class);
        var media = mock(WorkMediaCleanup.class);
        var manager = new PortfolioManager(
                new com.frameforward.portfolio.repository.PortfolioRepository(favorites, jobs), evaluations, media);
        when(favorites.selectCount(any())).thenReturn(1L);
        assertTrue(manager.favorite("account", "media"));
        when(favorites.selectOne(any())).thenReturn(null);
        manager.updateFavorite("account", "media", true);
        verify(favorites).insert(any(PortfolioFavoriteEntity.class));
        var favorite = new PortfolioFavoriteEntity();
        favorite.mediaId = "media";
        when(favorites.selectOne(any())).thenReturn(favorite);
        manager.updateFavorite("account", "media", false);
        verify(favorites).deleteById("media");
        when(jobs.selectCount(any())).thenReturn(1L);
        assertTrue(manager.deletionStarted("account", "media"));
        assertNull(manager.findDeletionJob("account", "media"));
        assertNull(manager.findDeletionJobById("account", "job"));
        var job = manager.createDeletionJob("account", "media");
        assertEquals("PENDING", job.state);
        verify(jobs).insert(job);
        manager.processDeletion(job);
        assertEquals("COMPLETED", job.state);
        verify(media).deleteForWork("account", "media");
        doThrow(new IllegalStateException()).when(evaluations).deleteForWork("account", "media");
        manager.processDeletion(job);
        assertEquals("FAILED", job.state);
        assertNotNull(job.failureReason);
    }

    @Test
    void businessRejectsMissingOrDeletingWorkAndHandlesJobStates() {
        var manager = mock(PortfolioManager.class);
        var business = new PortfolioBusiness(manager);
        when(manager.deletionStarted("account", "media")).thenReturn(true);
        assertThrows(PortfolioNotFound.class, () -> business.setFavorite("account", "media", true, true));
        when(manager.deletionStarted("account", "media")).thenReturn(false);
        assertThrows(PortfolioNotFound.class, () -> business.setFavorite("account", "media", true, false));
        assertEquals("media", business.setFavorite("account", "media", true, true).mediaId());
        verify(manager).updateFavorite("account", "media", true);
        assertThrows(PortfolioNotFound.class, () -> business.delete("account", "media", false));
        var job = new PortfolioWorkDeletionJobEntity();
        job.id = "job";
        job.mediaId = "media";
        job.state = "PENDING";
        when(manager.findDeletionJob("account", "media")).thenReturn(null);
        when(manager.createDeletionJob("account", "media")).thenReturn(job);
        assertEquals("PENDING", business.delete("account", "media", true).state());
        verify(manager).processDeletion(job);
        job.state = "COMPLETED";
        when(manager.findDeletionJob("account", "media")).thenReturn(job);
        business.delete("account", "media", true);
        verify(manager, times(1)).processDeletion(job);
        when(manager.findDeletionJobById("account", "missing")).thenReturn(null);
        assertThrows(PortfolioNotFound.class, () -> business.deletionStatus("account", "missing"));
        when(manager.findDeletionJobById("account", "job")).thenReturn(job);
        assertEquals("job", business.deletionStatus("account", "job").jobId());
    }
}
