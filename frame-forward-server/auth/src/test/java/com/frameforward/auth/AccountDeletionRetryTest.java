package com.frameforward.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class AccountDeletionRetryTest {
  @Test void failedCleanupRemainsRetryableAndIsNeverReportedComplete() {
    AccountMapper accounts = mock(AccountMapper.class); RefreshSessionMapper sessions = mock(RefreshSessionMapper.class); AccountDeletionJobMapper jobs = mock(AccountDeletionJobMapper.class);
    when(jobs.selectCount(any())).thenReturn(0L); when(accounts.deleteById(any(String.class))).thenReturn(1);
    RuntimeException failure = new RuntimeException("file locked");
    AccountDataCleanup cleanup = mock(AccountDataCleanup.class); doThrow(failure).doNothing().when(cleanup).deleteForAccount(any(String.class));
    AuthService auth = new AuthService(accounts, sessions, jobs, List.of(cleanup));
    AuthService.SessionTokens session = auth.register("retry_user", "retry@example.com", "ValidPass1!");

    AuthService.AccountDeletionJob failed = auth.startAccountDeletion(session.accessToken(), "ValidPass1!");
    assertThat(failed.state()).isEqualTo("FAILED");
    assertThat(failed.failureReason()).isNotBlank();
    verify(accounts, never()).deleteById(any(String.class));
    AccountDeletionJobEntity job = new AccountDeletionJobEntity(); job.id = failed.jobId(); job.accountId = "account"; job.state = "FAILED"; job.deletionTokenHash = tokenHash(failed.deletionToken()); job.deletionTokenExpiresAt = java.time.Instant.now().plusSeconds(60);
    when(jobs.selectById(job.id)).thenReturn(job);

    AuthService.AccountDeletionJob retried = auth.retryAccountDeletion(job.id, failed.deletionToken());
    assertThat(retried.state()).isEqualTo("COMPLETED");
    verify(accounts).deleteById("account");
  }

  private static String tokenHash(String token) { try { return java.util.Base64.getEncoder().encodeToString(java.security.MessageDigest.getInstance("SHA-256").digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8))); } catch (Exception exception) { throw new AssertionError(exception); } }
}
