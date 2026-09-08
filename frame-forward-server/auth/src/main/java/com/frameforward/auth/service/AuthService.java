package com.frameforward.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.auth.business.AuthBusiness;
import com.frameforward.auth.business.PasswordPolicy;
import com.frameforward.auth.component.BearerToken;
import com.frameforward.auth.manager.AuthManager;
import com.frameforward.auth.model.dto.AccountDeletionJob;
import com.frameforward.auth.model.dto.SessionTokens;
import com.frameforward.auth.model.entity.AccountDeletionJobEntity;
import com.frameforward.auth.model.entity.AccountEntity;
import com.frameforward.auth.model.entity.RefreshSessionEntity;

@Service
public class AuthService {
    private static final Duration ACCESS_TTL = Duration.ofMinutes(15), REFRESH_TTL = Duration.ofDays(30);
    private final AuthManager manager;
    private final AuthBusiness business;
    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder();
    private final Map<String, AccessGrant> accessGrants = new ConcurrentHashMap<>();
    private final Clock clock = Clock.systemUTC();
    public AuthService(AuthManager manager, AuthBusiness business) {
        this.manager = manager;
        this.business = business;
    }
    public static String bearer(String authorization) {
        String token = BearerToken.parse(authorization);
        if (token == null)
            throw new InvalidSessionException();
        return token;
    }
    @Transactional
    public SessionTokens register(String username, String email, String password) {
        var account = new AccountEntity(UUID.randomUUID().toString(), username(username), email(email),
                encode(password));
        try {
            manager.insertAccount(account);
        } catch (DuplicateKeyException exception) {
            throw new ConflictException();
        }
        return issue(account);
    }
    public SessionTokens login(String identifier, String password) {
        var normalized = identifier == null ? "" : identifier.trim().toLowerCase();
        var account = manager.findByIdentifier(normalized);
        if (account == null || deleting(account.id)
                || !passwords.matches(password == null ? "" : password, account.passwordHash))
            throw new InvalidCredentialsException();
        return issue(account);
    }
    @Transactional
    public SessionTokens refresh(String refreshToken) {
        var hash = hash(refreshToken);
        var session = manager.findSession(hash);
        if (session == null || session.expiresAt.isBefore(clock.instant()) || manager.deleteSession(hash) != 1)
            throw new InvalidSessionException();
        var account = manager.findAccount(session.accountId);
        if (account == null || deleting(account.id))
            throw new InvalidSessionException();
        return issue(account);
    }
    @Transactional
    public void logout(String accessToken) {
        revoke(access(accessToken).account.id);
    }
    @Transactional
    public void changePassword(String accessToken, String currentPassword, String newPassword) {
        var grant = access(accessToken);
        if (!passwords.matches(currentPassword == null ? "" : currentPassword, grant.account.passwordHash))
            throw new InvalidCredentialsException();
        grant.account.passwordHash = encode(newPassword);
        manager.updateAccount(grant.account);
        revoke(grant.account.id);
    }
    public String requireAccountId(String accessToken) {
        return access(accessToken).account.id;
    }
    public AccountDeletionJob startAccountDeletion(String accessToken, String currentPassword) {
        var grant = access(accessToken);
        if (!passwords.matches(currentPassword == null ? "" : currentPassword, grant.account.passwordHash))
            throw new InvalidCredentialsException();
        if (deleting(grant.account.id))
            throw new InvalidSessionException();
        String deletionToken = token();
        Instant now = clock.instant();
        var job = new AccountDeletionJobEntity();
        job.id = UUID.randomUUID().toString();
        job.accountId = grant.account.id;
        job.deletionTokenHash = hash(deletionToken);
        job.state = "PENDING";
        job.deletionTokenExpiresAt = now.plus(Duration.ofHours(24));
        job.createdAt = now;
        job.updatedAt = now;
        manager.insertDeletionJob(job);
        revoke(grant.account.id);
        business.processDeletion(job);
        return deletionJob(job, deletionToken);
    }
    public AccountDeletionJob deletionStatus(String jobId, String deletionToken) {
        return deletionJob(authorize(jobId, deletionToken), null);
    }
    public AccountDeletionJob retryAccountDeletion(String jobId, String deletionToken) {
        var job = authorize(jobId, deletionToken);
        if (!"COMPLETED".equals(job.state))
            business.processDeletion(job);
        return deletionJob(job, null);
    }
    private SessionTokens issue(AccountEntity account) {
        var now = clock.instant();
        var access = token();
        var refresh = token();
        accessGrants.put(access, new AccessGrant(account, now.plus(ACCESS_TTL)));
        manager.insertSession(new RefreshSessionEntity(hash(refresh), account.id, now.plus(REFRESH_TTL)));
        return new SessionTokens(access, refresh, ACCESS_TTL.toSeconds());
    }
    private AccessGrant access(String token) {
        var grant = accessGrants.get(token);
        if (grant == null || grant.expiresAt.isBefore(clock.instant()) || deleting(grant.account.id)) {
            accessGrants.remove(token);
            throw new InvalidSessionException();
        }
        return grant;
    }
    private void revoke(String accountId) {
        accessGrants.entrySet().removeIf(e -> e.getValue().account.id.equals(accountId));
        manager.deleteSessionsForAccount(accountId);
    }
    private boolean deleting(String accountId) {
        return manager.hasUnfinishedDeletion(accountId);
    }
    private AccountDeletionJobEntity authorize(String jobId, String deletionToken) {
        var job = manager.findDeletionJob(jobId);
        if (job == null || deletionToken == null || deletionToken.isBlank()
                || job.deletionTokenExpiresAt.isBefore(clock.instant())
                || !hash(deletionToken).equals(job.deletionTokenHash))
            throw new InvalidSessionException();
        return job;
    }
    private static AccountDeletionJob deletionJob(AccountDeletionJobEntity job, String deletionToken) {
        return new AccountDeletionJob(job.id, job.state, job.failureReason, deletionToken, job.deletionTokenExpiresAt);
    }
    private String encode(String password) {
        try {
            PasswordPolicy.validate(password);
        } catch (PasswordPolicy.InvalidPasswordException exception) {
            throw new ValidationException(exception.getMessage());
        }
        return passwords.encode(password);
    }
    private static String username(String value) {
        if (value == null || !value.trim().matches("[A-Za-z0-9_]{3,32}"))
            throw new ValidationException("username must be 3-32 letters, numbers, or underscores");
        return value.trim().toLowerCase();
    }
    private static String email(String value) {
        if (value == null || !value.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))
            throw new ValidationException("email must be valid");
        return value.trim().toLowerCase();
    }
    private static String token() {
        var bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private static String hash(String token) {
        try {
            return Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    private record AccessGrant(AccountEntity account, Instant expiresAt) {
    }
    public static final class ConflictException extends RuntimeException {
    }
    public static final class InvalidCredentialsException extends RuntimeException {
    }
    public static final class InvalidSessionException extends RuntimeException {
    }
    public static final class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }
}
