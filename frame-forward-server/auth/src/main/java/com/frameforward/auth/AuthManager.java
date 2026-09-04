package com.frameforward.auth;

import org.springframework.stereotype.Component;

/** 认证模块唯一的 Mapper 访问边界。 */
@Component
public class AuthManager {
    private final AccountMapper accounts;
    private final RefreshSessionMapper sessions;
    private final AccountDeletionJobMapper deletionJobs;

    public AuthManager(AccountMapper accounts, RefreshSessionMapper sessions, AccountDeletionJobMapper deletionJobs) {
        this.accounts = accounts;
        this.sessions = sessions;
        this.deletionJobs = deletionJobs;
    }

    AccountMapper accounts() {
        return accounts;
    }
    RefreshSessionMapper sessions() {
        return sessions;
    }
    AccountDeletionJobMapper deletionJobs() {
        return deletionJobs;
    }
}
