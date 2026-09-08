package com.frameforward.portfolio.manager;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.frameforward.evaluation.gateway.WorkEvaluationCleanup;
import com.frameforward.media.gateway.WorkMediaCleanup;
import com.frameforward.portfolio.model.entity.PortfolioFavoriteEntity;
import com.frameforward.portfolio.model.entity.PortfolioWorkDeletionJobEntity;
import com.frameforward.portfolio.repository.PortfolioRepository;

@Component
public class PortfolioManager {
    private final PortfolioRepository repository;
    private final WorkEvaluationCleanup evaluationCleanup;
    private final WorkMediaCleanup mediaCleanup;

    public PortfolioManager(PortfolioRepository repository, WorkEvaluationCleanup evaluationCleanup,
            WorkMediaCleanup mediaCleanup) {
        this.repository = repository;
        this.evaluationCleanup = evaluationCleanup;
        this.mediaCleanup = mediaCleanup;
    }

    public boolean favorite(String accountId, String mediaId) {
        return repository.countFavorites(accountId, mediaId) > 0;
    }

    public void updateFavorite(String accountId, String mediaId, boolean value) {
        PortfolioFavoriteEntity current = repository.findFavorite(accountId, mediaId);
        if (value && current == null) {
            PortfolioFavoriteEntity created = new PortfolioFavoriteEntity();
            created.mediaId = mediaId;
            created.accountId = accountId;
            created.createdAt = Instant.now();
            repository.saveFavorite(created);
        }
        if (!value && current != null)
            repository.deleteFavoriteById(current.mediaId);
    }

    public boolean deletionStarted(String accountId, String mediaId) {
        return repository.countDeletionJobs(accountId, mediaId) > 0;
    }

    public PortfolioWorkDeletionJobEntity findDeletionJob(String accountId, String mediaId) {
        return repository.findDeletionJob(accountId, mediaId);
    }

    public PortfolioWorkDeletionJobEntity findDeletionJobById(String accountId, String jobId) {
        return repository.findDeletionJobById(accountId, jobId);
    }

    public PortfolioWorkDeletionJobEntity createDeletionJob(String accountId, String mediaId) {
        PortfolioWorkDeletionJobEntity job = new PortfolioWorkDeletionJobEntity();
        job.id = UUID.randomUUID().toString();
        job.accountId = accountId;
        job.mediaId = mediaId;
        job.state = "PENDING";
        job.createdAt = Instant.now();
        job.updatedAt = job.createdAt;
        repository.saveDeletionJob(job);
        return job;
    }

    public void processDeletion(PortfolioWorkDeletionJobEntity job) {
        job.state = "IN_PROGRESS";
        job.failureReason = null;
        job.updatedAt = Instant.now();
        repository.updateDeletionJob(job);
        try {
            evaluationCleanup.deleteForWork(job.accountId, job.mediaId);
            repository.deleteFavorite(job.accountId, job.mediaId);
            mediaCleanup.deleteForWork(job.accountId, job.mediaId);
            job.state = "COMPLETED";
        } catch (RuntimeException exception) {
            job.state = "FAILED";
            job.failureReason = "关联数据清理失败，请重试。";
        }
        job.updatedAt = Instant.now();
        repository.updateDeletionJob(job);
    }

}
