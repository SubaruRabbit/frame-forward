package com.frameforward.auth.repository;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.auth.mapper.AccountDeletionJobMapper;
import com.frameforward.auth.mapper.AccountMapper;
import com.frameforward.auth.mapper.RefreshSessionMapper;
import com.frameforward.auth.model.entity.AccountDeletionJobEntity;
import com.frameforward.auth.model.entity.AccountEntity;
import com.frameforward.auth.model.entity.RefreshSessionEntity;

@Repository
public class AuthRepository {
    private final AccountMapper accounts;
    private final RefreshSessionMapper sessions;
    private final AccountDeletionJobMapper jobs;

    public AuthRepository(AccountMapper accounts, RefreshSessionMapper sessions, AccountDeletionJobMapper jobs) {
        this.accounts = accounts;
        this.sessions = sessions;
        this.jobs = jobs;
    }

    public void insertAccount(AccountEntity account) {
        accounts.insert(account);
    }
    public void updateAccount(AccountEntity account) {
        accounts.updateById(account);
    }
    public AccountEntity findAccount(String id) {
        return accounts.selectById(id);
    }
    public int deleteAccount(String id) {
        return accounts.deleteById(id);
    }
    public AccountEntity findByIdentifier(String identifier) {
        return accounts.selectOne(new LambdaQueryWrapper<AccountEntity>().eq(AccountEntity::getUsername, identifier)
                .or().eq(AccountEntity::getEmail, identifier));
    }
    public void insertSession(RefreshSessionEntity session) {
        sessions.insert(session);
    }
    public RefreshSessionEntity findSession(String hash) {
        return sessions.selectById(hash);
    }
    public int deleteSession(String hash) {
        return sessions.deleteById(hash);
    }
    public void deleteSessionsForAccount(String accountId) {
        sessions.delete(
                new LambdaQueryWrapper<RefreshSessionEntity>().eq(RefreshSessionEntity::getAccountId, accountId));
    }
    public void insertDeletionJob(AccountDeletionJobEntity job) {
        jobs.insert(job);
    }
    public void updateDeletionJob(AccountDeletionJobEntity job) {
        jobs.updateById(job);
    }
    public AccountDeletionJobEntity findDeletionJob(String id) {
        return jobs.selectById(id);
    }
    public boolean hasUnfinishedDeletion(String accountId) {
        return jobs.selectCount(
                new LambdaQueryWrapper<AccountDeletionJobEntity>().eq(AccountDeletionJobEntity::getAccountId, accountId)
                        .ne(AccountDeletionJobEntity::getState, "COMPLETED")) > 0;
    }
}
