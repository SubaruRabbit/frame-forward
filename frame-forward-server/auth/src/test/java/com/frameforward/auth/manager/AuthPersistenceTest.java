package com.frameforward.auth.manager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.frameforward.auth.business.AuthBusiness;
import com.frameforward.auth.gateway.AccountDataCleanup;
import com.frameforward.auth.mapper.AccountDeletionJobMapper;
import com.frameforward.auth.mapper.AccountMapper;
import com.frameforward.auth.mapper.RefreshSessionMapper;
import com.frameforward.auth.model.entity.AccountDeletionJobEntity;
import com.frameforward.auth.model.entity.AccountEntity;
import com.frameforward.auth.model.entity.RefreshSessionEntity;
import com.frameforward.auth.repository.AuthRepository;

class AuthPersistenceTest {
    private final AccountMapper accounts = mock(AccountMapper.class);
    private final RefreshSessionMapper sessions = mock(RefreshSessionMapper.class);
    private final AccountDeletionJobMapper jobs = mock(AccountDeletionJobMapper.class);
    private final AuthRepository repository = new AuthRepository(accounts, sessions, jobs);
    private final AuthManager manager = new AuthManager(repository, List.of());

    @Test
    void accountLookupAndPasswordUpdatePreserveIdentity() {
        var account = new AccountEntity("account", "user", "user@example.com", "hash");
        when(accounts.selectById("account")).thenReturn(account);
        when(accounts.selectOne(any())).thenReturn(account);
        assertSame(account, manager.findAccount("account"));
        assertSame(account, manager.findByIdentifier("user@example.com"));
        account.passwordHash = "replacement";
        manager.updateAccount(account);
        verify(accounts).updateById(account);
    }

    @Test
    void refreshConsumptionReportsAffectedRowsAndRevocationDeletesSessions() {
        var session = new RefreshSessionEntity("hash", "account", Instant.MAX);
        when(sessions.selectById("hash")).thenReturn(session);
        when(sessions.deleteById("hash")).thenReturn(1, 0);
        assertSame(session, manager.findSession("hash"));
        assertEquals(1, manager.deleteSession("hash"));
        assertEquals(0, manager.deleteSession("hash"));
        manager.deleteSessionsForAccount("account");
        verify(sessions).delete(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class));
    }

    @Test
    void missingCleanupOrAccountCannotCompleteDeletion() {
        var job = new AccountDeletionJobEntity();
        job.id = "job";
        job.accountId = "account";
        new AuthBusiness(manager).processDeletion(job);
        assertEquals("FAILED", job.state);
        verify(accounts, never()).deleteById(anyString());

        AccountDataCleanup cleanup = mock(AccountDataCleanup.class);
        new AuthBusiness(new AuthManager(repository, List.of(cleanup))).processDeletion(job);
        verify(cleanup).deleteForAccount("account");
        verify(accounts).deleteById("account");
        assertEquals("FAILED", job.state);
        assertEquals("账号数据清理失败，请重试。", job.failureReason);
    }
}
