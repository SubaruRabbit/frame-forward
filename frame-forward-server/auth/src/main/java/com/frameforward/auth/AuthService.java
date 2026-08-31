package com.frameforward.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final Duration ACCESS_TTL = Duration.ofMinutes(15), REFRESH_TTL = Duration.ofDays(30);
    private final AccountMapper accounts; private final RefreshSessionMapper sessions; private final AccountDeletionJobMapper deletionJobs; private final List<AccountDataCleanup> cleanups;
    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder();
    private final Map<String, AccessGrant> accessGrants = new ConcurrentHashMap<>();
    private final Clock clock = Clock.systemUTC();
    public AuthService(AccountMapper accounts, RefreshSessionMapper sessions, AccountDeletionJobMapper deletionJobs, List<AccountDataCleanup> cleanups) { this.accounts = accounts; this.sessions = sessions; this.deletionJobs = deletionJobs; this.cleanups = cleanups; }
    @Transactional public SessionTokens register(String username, String email, String password) {
        var account = new AccountEntity(UUID.randomUUID().toString(), username(username), email(email), encode(password));
        try { accounts.insert(account); } catch (DuplicateKeyException exception) { throw new ConflictException(); }
        return issue(account);
    }
    public SessionTokens login(String identifier, String password) {
        var normalized = identifier == null ? "" : identifier.trim().toLowerCase();
        var account = accounts.selectOne(new LambdaQueryWrapper<AccountEntity>().eq(AccountEntity::getUsername, normalized).or().eq(AccountEntity::getEmail, normalized));
        if (account == null || deleting(account.id) || !passwords.matches(password == null ? "" : password, account.passwordHash)) throw new InvalidCredentialsException(); return issue(account);
    }
    @Transactional public SessionTokens refresh(String refreshToken) {
        var hash = hash(refreshToken); var session = sessions.selectById(hash);
        if (session == null || session.expiresAt.isBefore(clock.instant()) || sessions.deleteById(hash) != 1) throw new InvalidSessionException();
        var account = accounts.selectById(session.accountId); if (account == null || deleting(account.id)) throw new InvalidSessionException(); return issue(account);
    }
    @Transactional public void logout(String accessToken) { revoke(access(accessToken).account.id); }
    @Transactional public void changePassword(String accessToken, String currentPassword, String newPassword) {
        var grant = access(accessToken); if (!passwords.matches(currentPassword == null ? "" : currentPassword, grant.account.passwordHash)) throw new InvalidCredentialsException(); grant.account.passwordHash = encode(newPassword); accounts.updateById(grant.account); revoke(grant.account.id);
    }
    public String requireAccountId(String accessToken) { return access(accessToken).account.id; }
    public AccountDeletionJob startAccountDeletion(String accessToken, String currentPassword) {
        var grant = access(accessToken);
        if (!passwords.matches(currentPassword == null ? "" : currentPassword, grant.account.passwordHash)) throw new InvalidCredentialsException();
        if (deleting(grant.account.id)) throw new InvalidSessionException();
        String deletionToken = token(); Instant now = clock.instant();
        var job = new AccountDeletionJobEntity(); job.id = UUID.randomUUID().toString(); job.accountId = grant.account.id; job.deletionTokenHash = hash(deletionToken); job.state = "PENDING"; job.deletionTokenExpiresAt = now.plus(Duration.ofHours(24)); job.createdAt = now; job.updatedAt = now;
        deletionJobs.insert(job); revoke(grant.account.id); process(job); return deletionJob(job, deletionToken);
    }
    public AccountDeletionJob deletionStatus(String jobId, String deletionToken) { return deletionJob(authorize(jobId, deletionToken), null); }
    public AccountDeletionJob retryAccountDeletion(String jobId, String deletionToken) { var job = authorize(jobId, deletionToken); if (!"COMPLETED".equals(job.state)) process(job); return deletionJob(job, null); }
    private SessionTokens issue(AccountEntity account) { var now = clock.instant(); var access = token(); var refresh = token(); accessGrants.put(access, new AccessGrant(account, now.plus(ACCESS_TTL))); sessions.insert(new RefreshSessionEntity(hash(refresh), account.id, now.plus(REFRESH_TTL))); return new SessionTokens(access, refresh, ACCESS_TTL.toSeconds()); }
    private AccessGrant access(String token) { var grant = accessGrants.get(token); if (grant == null || grant.expiresAt.isBefore(clock.instant()) || deleting(grant.account.id)) { accessGrants.remove(token); throw new InvalidSessionException(); } return grant; }
    private void revoke(String accountId) { accessGrants.entrySet().removeIf(e -> e.getValue().account.id.equals(accountId)); sessions.delete(new LambdaQueryWrapper<RefreshSessionEntity>().eq(RefreshSessionEntity::getAccountId, accountId)); }
    private boolean deleting(String accountId) { return deletionJobs.selectCount(new LambdaQueryWrapper<AccountDeletionJobEntity>().eq(AccountDeletionJobEntity::getAccountId, accountId).ne(AccountDeletionJobEntity::getState, "COMPLETED")) > 0; }
    private AccountDeletionJobEntity authorize(String jobId, String deletionToken) { var job = deletionJobs.selectById(jobId); if (job == null || deletionToken == null || deletionToken.isBlank() || job.deletionTokenExpiresAt.isBefore(clock.instant()) || !hash(deletionToken).equals(job.deletionTokenHash)) throw new InvalidSessionException(); return job; }
    private void process(AccountDeletionJobEntity job) {
        job.state = "IN_PROGRESS"; job.failureReason = null; job.updatedAt = clock.instant(); deletionJobs.updateById(job);
        try { cleanups.forEach(cleanup -> cleanup.deleteForAccount(job.accountId)); if (cleanups.isEmpty()) throw new IllegalStateException("缺少账户数据清理器"); if (accounts.deleteById(job.accountId) != 1) throw new IllegalStateException("账号不存在"); job.state = "COMPLETED"; }
        catch (RuntimeException exception) { job.state = "FAILED"; job.failureReason = "账号数据清理失败，请重试。"; }
        job.updatedAt = clock.instant(); deletionJobs.updateById(job);
    }
    private static AccountDeletionJob deletionJob(AccountDeletionJobEntity job, String deletionToken) { return new AccountDeletionJob(job.id, job.state, job.failureReason, deletionToken, job.deletionTokenExpiresAt); }
    private String encode(String password) { PasswordPolicy.validate(password); return passwords.encode(password); }
    private static String username(String value) { if (value == null || !value.trim().matches("[A-Za-z0-9_]{3,32}")) throw new ValidationException("username must be 3-32 letters, numbers, or underscores"); return value.trim().toLowerCase(); }
    private static String email(String value) { if (value == null || !value.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) throw new ValidationException("email must be valid"); return value.trim().toLowerCase(); }
    private static String token() { var bytes = new byte[32]; new SecureRandom().nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
    private static String hash(String token) { try { return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); } catch (Exception e) { throw new IllegalStateException(e); } }
    public record SessionTokens(String accessToken, String refreshToken, long expiresIn) {}
    public record AccountDeletionJob(String jobId, String state, String failureReason, String deletionToken, Instant deletionTokenExpiresAt) {}
    private record AccessGrant(AccountEntity account, Instant expiresAt) {}
    public static final class ConflictException extends RuntimeException {} public static final class InvalidCredentialsException extends RuntimeException {} public static final class InvalidSessionException extends RuntimeException {} public static final class ValidationException extends RuntimeException { public ValidationException(String message) { super(message); } }
}
