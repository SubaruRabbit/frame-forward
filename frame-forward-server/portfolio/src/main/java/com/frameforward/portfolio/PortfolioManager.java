package com.frameforward.portfolio;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.evaluation.WorkEvaluationCleanup;
import com.frameforward.media.WorkMediaCleanup;

@Component
class PortfolioManager {
    private final PortfolioFavoriteMapper favorites;
    private final PortfolioWorkDeletionJobMapper deletionJobs;
    private final WorkEvaluationCleanup evaluationCleanup;
    private final WorkMediaCleanup mediaCleanup;

    PortfolioManager(PortfolioFavoriteMapper favorites, PortfolioWorkDeletionJobMapper deletionJobs,
            WorkEvaluationCleanup evaluationCleanup, WorkMediaCleanup mediaCleanup) {
        this.favorites = favorites;
        this.deletionJobs = deletionJobs;
        this.evaluationCleanup = evaluationCleanup;
        this.mediaCleanup = mediaCleanup;
    }

    boolean favorite(String accountId, String mediaId) {
        return favorites.selectCount(favoriteQuery(accountId, mediaId)) > 0;
    }

    void updateFavorite(String accountId, String mediaId, boolean value) {
        PortfolioFavoriteEntity current = favorites.selectOne(favoriteQuery(accountId, mediaId));
        if (value && current == null) {
            PortfolioFavoriteEntity created = new PortfolioFavoriteEntity();
            created.mediaId = mediaId;
            created.accountId = accountId;
            created.createdAt = Instant.now();
            favorites.insert(created);
        }
        if (!value && current != null)
            favorites.deleteById(current.mediaId);
    }

    boolean deletionStarted(String accountId, String mediaId) {
        return deletionJobs.selectCount(deletionJobQuery(accountId, mediaId)) > 0;
    }

    PortfolioWorkDeletionJobEntity findDeletionJob(String accountId, String mediaId) {
        return deletionJobs.selectOne(deletionJobQuery(accountId, mediaId));
    }

    PortfolioWorkDeletionJobEntity findDeletionJobById(String accountId, String jobId) {
        return deletionJobs.selectOne(new LambdaQueryWrapper<PortfolioWorkDeletionJobEntity>()
                .eq(PortfolioWorkDeletionJobEntity::getId, jobId)
                .eq(PortfolioWorkDeletionJobEntity::getAccountId, accountId));
    }

    PortfolioWorkDeletionJobEntity createDeletionJob(String accountId, String mediaId) {
        PortfolioWorkDeletionJobEntity job = new PortfolioWorkDeletionJobEntity();
        job.id = UUID.randomUUID().toString();
        job.accountId = accountId;
        job.mediaId = mediaId;
        job.state = "PENDING";
        job.createdAt = Instant.now();
        job.updatedAt = job.createdAt;
        deletionJobs.insert(job);
        return job;
    }

    void processDeletion(PortfolioWorkDeletionJobEntity job) {
        job.state = "IN_PROGRESS";
        job.failureReason = null;
        job.updatedAt = Instant.now();
        deletionJobs.updateById(job);
        try {
            evaluationCleanup.deleteForWork(job.accountId, job.mediaId);
            favorites.delete(favoriteQuery(job.accountId, job.mediaId));
            mediaCleanup.deleteForWork(job.accountId, job.mediaId);
            job.state = "COMPLETED";
        } catch (RuntimeException exception) {
            job.state = "FAILED";
            job.failureReason = "关联数据清理失败，请重试。";
        }
        job.updatedAt = Instant.now();
        deletionJobs.updateById(job);
    }

    private static LambdaQueryWrapper<PortfolioFavoriteEntity> favoriteQuery(String accountId, String mediaId) {
        return new LambdaQueryWrapper<PortfolioFavoriteEntity>().eq(PortfolioFavoriteEntity::getMediaId, mediaId)
                .eq(PortfolioFavoriteEntity::getAccountId, accountId);
    }

    private static LambdaQueryWrapper<PortfolioWorkDeletionJobEntity> deletionJobQuery(String accountId,
            String mediaId) {
        return new LambdaQueryWrapper<PortfolioWorkDeletionJobEntity>()
                .eq(PortfolioWorkDeletionJobEntity::getAccountId, accountId)
                .eq(PortfolioWorkDeletionJobEntity::getMediaId, mediaId);
    }
}
