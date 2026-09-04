package com.frameforward.auth;

import java.time.Clock;
import java.util.List;

import org.springframework.stereotype.Component;

/** 账号删除的跨资源业务规则与状态转换。 */
@Component
public class AuthBusiness {
    private final AuthManager manager;
    private final List<AccountDataCleanup> cleanups;
    private final Clock clock = Clock.systemUTC();

    public AuthBusiness(AuthManager manager, List<AccountDataCleanup> cleanups) {
        this.manager = manager;
        this.cleanups = cleanups;
    }

    public void processDeletion(AccountDeletionJobEntity job) {
        job.state = "IN_PROGRESS";
        job.failureReason = null;
        job.updatedAt = clock.instant();
        manager.deletionJobs().updateById(job);
        try {
            cleanups.forEach(cleanup -> cleanup.deleteForAccount(job.accountId));
            if (cleanups.isEmpty())
                throw new IllegalStateException("缺少账户数据清理器");
            if (manager.accounts().deleteById(job.accountId) != 1)
                throw new IllegalStateException("账号不存在");
            job.state = "COMPLETED";
        } catch (RuntimeException exception) {
            job.state = "FAILED";
            job.failureReason = "账号数据清理失败，请重试。";
        }
        job.updatedAt = clock.instant();
        manager.deletionJobs().updateById(job);
    }
}
