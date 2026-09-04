package com.frameforward.portfolio;

import org.springframework.stereotype.Component;

@Component
class PortfolioBusiness {
    private final PortfolioManager manager;

    PortfolioBusiness(PortfolioManager manager) {
        this.manager = manager;
    }

    boolean favorite(String accountId, String mediaId) {
        return manager.favorite(accountId, mediaId);
    }

    boolean deletionStarted(String accountId, String mediaId) {
        return manager.deletionStarted(accountId, mediaId);
    }

    PortfolioService.Favorite setFavorite(String accountId, String mediaId, boolean value, boolean mediaOwned) {
        if (deletionStarted(accountId, mediaId) || !mediaOwned)
            throw new PortfolioService.NotFound();
        manager.updateFavorite(accountId, mediaId, value);
        return new PortfolioService.Favorite(mediaId, value);
    }

    PortfolioService.DeletionJob delete(String accountId, String mediaId, boolean mediaOwned) {
        PortfolioWorkDeletionJobEntity job = manager.findDeletionJob(accountId, mediaId);
        if (job == null) {
            if (!mediaOwned)
                throw new PortfolioService.NotFound();
            job = manager.createDeletionJob(accountId, mediaId);
        }
        if (!"COMPLETED".equals(job.state))
            manager.processDeletion(job);
        return deletionJob(job);
    }

    PortfolioService.DeletionJob deletionStatus(String accountId, String jobId) {
        PortfolioWorkDeletionJobEntity job = manager.findDeletionJobById(accountId, jobId);
        if (job == null)
            throw new PortfolioService.NotFound();
        return deletionJob(job);
    }

    private static PortfolioService.DeletionJob deletionJob(PortfolioWorkDeletionJobEntity job) {
        return new PortfolioService.DeletionJob(job.id, job.mediaId, job.state, job.failureReason);
    }
}
