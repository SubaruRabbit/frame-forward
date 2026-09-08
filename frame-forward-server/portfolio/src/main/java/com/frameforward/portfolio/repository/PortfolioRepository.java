package com.frameforward.portfolio.repository;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.portfolio.mapper.PortfolioFavoriteMapper;
import com.frameforward.portfolio.mapper.PortfolioWorkDeletionJobMapper;
import com.frameforward.portfolio.model.entity.PortfolioFavoriteEntity;
import com.frameforward.portfolio.model.entity.PortfolioWorkDeletionJobEntity;
@Repository
public class PortfolioRepository {
    private final PortfolioFavoriteMapper favorites;
    private final PortfolioWorkDeletionJobMapper deletionJobs;
    public PortfolioRepository(PortfolioFavoriteMapper favorites, PortfolioWorkDeletionJobMapper deletionJobs) {
        this.favorites = favorites;
        this.deletionJobs = deletionJobs;
    }
    public long countFavorites(String accountId, String mediaId) {
        return favorites.selectCount(favoriteQuery(accountId, mediaId));
    }
    public PortfolioFavoriteEntity findFavorite(String accountId, String mediaId) {
        return favorites.selectOne(favoriteQuery(accountId, mediaId));
    }
    public void saveFavorite(PortfolioFavoriteEntity favorite) {
        favorites.insert(favorite);
    }
    public void deleteFavoriteById(String mediaId) {
        favorites.deleteById(mediaId);
    }
    public void deleteFavorite(String accountId, String mediaId) {
        favorites.delete(favoriteQuery(accountId, mediaId));
    }
    public long countDeletionJobs(String accountId, String mediaId) {
        return deletionJobs.selectCount(deletionJobQuery(accountId, mediaId));
    }
    public PortfolioWorkDeletionJobEntity findDeletionJob(String accountId, String mediaId) {
        return deletionJobs.selectOne(deletionJobQuery(accountId, mediaId));
    }
    public PortfolioWorkDeletionJobEntity findDeletionJobById(String accountId, String jobId) {
        return deletionJobs.selectOne(new LambdaQueryWrapper<PortfolioWorkDeletionJobEntity>()
                .eq(PortfolioWorkDeletionJobEntity::getId, jobId)
                .eq(PortfolioWorkDeletionJobEntity::getAccountId, accountId));
    }
    public void saveDeletionJob(PortfolioWorkDeletionJobEntity job) {
        deletionJobs.insert(job);
    }
    public void updateDeletionJob(PortfolioWorkDeletionJobEntity job) {
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
