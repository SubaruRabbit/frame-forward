package com.frameforward.auth.manager;

import java.util.List;

import org.springframework.stereotype.Component;

import com.frameforward.auth.gateway.AccountDataCleanup;
import com.frameforward.auth.model.entity.AccountDeletionJobEntity;
import com.frameforward.auth.model.entity.AccountEntity;
import com.frameforward.auth.model.entity.RefreshSessionEntity;
import com.frameforward.auth.repository.AuthRepository;

/** 对上层隐藏持久化实现，并协调账户的跨资源清理。 */
@Component
public class AuthManager {
    private final AuthRepository repository;
    private final List<AccountDataCleanup> cleanups;
    public AuthManager(AuthRepository repository, List<AccountDataCleanup> cleanups) {
        this.repository = repository;
        this.cleanups = List.copyOf(cleanups);
    }
    public void insertAccount(AccountEntity account) {
        repository.insertAccount(account);
    }
    public void updateAccount(AccountEntity account) {
        repository.updateAccount(account);
    }
    public AccountEntity findAccount(String id) {
        return repository.findAccount(id);
    }
    public int deleteAccount(String id) {
        return repository.deleteAccount(id);
    }
    public AccountEntity findByIdentifier(String identifier) {
        return repository.findByIdentifier(identifier);
    }
    public void insertSession(RefreshSessionEntity session) {
        repository.insertSession(session);
    }
    public RefreshSessionEntity findSession(String hash) {
        return repository.findSession(hash);
    }
    public int deleteSession(String hash) {
        return repository.deleteSession(hash);
    }
    public void deleteSessionsForAccount(String accountId) {
        repository.deleteSessionsForAccount(accountId);
    }
    public void insertDeletionJob(AccountDeletionJobEntity job) {
        repository.insertDeletionJob(job);
    }
    public void updateDeletionJob(AccountDeletionJobEntity job) {
        repository.updateDeletionJob(job);
    }
    public AccountDeletionJobEntity findDeletionJob(String id) {
        return repository.findDeletionJob(id);
    }
    public boolean hasUnfinishedDeletion(String accountId) {
        return repository.hasUnfinishedDeletion(accountId);
    }
    public void cleanupAccountData(String accountId) {
        cleanups.forEach(cleanup -> cleanup.deleteForAccount(accountId));
        if (cleanups.isEmpty())
            throw new IllegalStateException("缺少账户数据清理器");
    }
}
