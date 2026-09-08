package com.frameforward.portfolio.business;
import org.springframework.stereotype.Component;

import com.frameforward.portfolio.manager.PortfolioManager;
import com.frameforward.portfolio.model.dto.DeletionJob;
import com.frameforward.portfolio.model.dto.Favorite;
import com.frameforward.portfolio.model.entity.PortfolioWorkDeletionJobEntity;

@Component
public class PortfolioBusiness {
    private final PortfolioManager manager;

    public PortfolioBusiness(PortfolioManager manager) {
        this.manager = manager;
    }

    public boolean favorite(String accountId, String mediaId) {
        return manager.favorite(accountId, mediaId);
    }

    public boolean deletionStarted(String accountId, String mediaId) {
        return manager.deletionStarted(accountId, mediaId);
    }

    public Favorite setFavorite(String accountId, String mediaId, boolean value, boolean mediaOwned) {
        if (deletionStarted(accountId, mediaId) || !mediaOwned)
            throw new PortfolioNotFound();
        manager.updateFavorite(accountId, mediaId, value);
        return new Favorite(mediaId, value);
    }

    public DeletionJob delete(String accountId, String mediaId, boolean mediaOwned) {
        PortfolioWorkDeletionJobEntity job = manager.findDeletionJob(accountId, mediaId);
        if (job == null) {
            if (!mediaOwned)
                throw new PortfolioNotFound();
            job = manager.createDeletionJob(accountId, mediaId);
        }
        if (!"COMPLETED".equals(job.state))
            manager.processDeletion(job);
        return deletionJob(job);
    }

    public DeletionJob deletionStatus(String accountId, String jobId) {
        PortfolioWorkDeletionJobEntity job = manager.findDeletionJobById(accountId, jobId);
        if (job == null)
            throw new PortfolioNotFound();
        return deletionJob(job);
    }

    private static DeletionJob deletionJob(PortfolioWorkDeletionJobEntity job) {
        return new DeletionJob(job.id, job.mediaId, job.state, job.failureReason);
    }
}
