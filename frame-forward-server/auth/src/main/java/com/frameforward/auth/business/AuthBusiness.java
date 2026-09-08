package com.frameforward.auth.business;

import java.time.Clock;

import org.springframework.stereotype.Component;

import com.frameforward.auth.manager.AuthManager;
import com.frameforward.auth.model.entity.AccountDeletionJobEntity;

/** 账号删除的跨资源业务规则与状态转换。 */
@Component
public class AuthBusiness {
    private final AuthManager manager;
    private final Clock clock = Clock.systemUTC();

    public AuthBusiness(AuthManager manager) {
        this.manager = manager;
    }

    public void processDeletion(AccountDeletionJobEntity job) {
        job.state = "IN_PROGRESS";
        job.failureReason = null;
        job.updatedAt = clock.instant();
        manager.updateDeletionJob(job);
        try {
            manager.cleanupAccountData(job.accountId);
            if (manager.deleteAccount(job.accountId) != 1)
                throw new IllegalStateException("账号不存在");
            job.state = "COMPLETED";
        } catch (RuntimeException exception) {
            job.state = "FAILED";
            job.failureReason = "账号数据清理失败，请重试。";
        }
        job.updatedAt = clock.instant();
        manager.updateDeletionJob(job);
    }
}
