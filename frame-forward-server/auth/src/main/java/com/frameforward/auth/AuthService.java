package com.frameforward.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

@Service
public class AuthService {
    private static final Duration ACCESS_TTL = Duration.ofMinutes(15), REFRESH_TTL = Duration.ofDays(30);
    private final AccountMapper accounts; private final RefreshSessionMapper sessions;
    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder();
    private final Map<String, AccessGrant> accessGrants = new ConcurrentHashMap<>();
    private final Clock clock = Clock.systemUTC();
    public AuthService(AccountMapper accounts, RefreshSessionMapper sessions) { this.accounts = accounts; this.sessions = sessions; }
    @Transactional public SessionTokens register(String username, String email, String password) {
        var account = new AccountEntity(UUID.randomUUID().toString(), username(username), email(email), encode(password));
        try { accounts.insert(account); } catch (DuplicateKeyException exception) { throw new ConflictException(); }
        return issue(account);
    }
    public SessionTokens login(String identifier, String password) {
        var normalized = identifier == null ? "" : identifier.trim().toLowerCase();
        var account = accounts.selectOne(new LambdaQueryWrapper<AccountEntity>().eq(AccountEntity::getUsername, normalized).or().eq(AccountEntity::getEmail, normalized));
        if (account == null || !passwords.matches(password == null ? "" : password, account.passwordHash)) throw new InvalidCredentialsException(); return issue(account);
    }
    @Transactional public SessionTokens refresh(String refreshToken) {
        var hash = hash(refreshToken); var session = sessions.selectById(hash);
        if (session == null || session.expiresAt.isBefore(clock.instant()) || sessions.deleteById(hash) != 1) throw new InvalidSessionException();
        var account = accounts.selectById(session.accountId); if (account == null) throw new InvalidSessionException(); return issue(account);
    }
    @Transactional public void logout(String accessToken) { revoke(access(accessToken).account.id); }
    @Transactional public void changePassword(String accessToken, String currentPassword, String newPassword) {
        var grant = access(accessToken); if (!passwords.matches(currentPassword == null ? "" : currentPassword, grant.account.passwordHash)) throw new InvalidCredentialsException(); grant.account.passwordHash = encode(newPassword); accounts.updateById(grant.account); revoke(grant.account.id);
    }
    public String requireAccountId(String accessToken) { return access(accessToken).account.id; }
    private SessionTokens issue(AccountEntity account) { var now = clock.instant(); var access = token(); var refresh = token(); accessGrants.put(access, new AccessGrant(account, now.plus(ACCESS_TTL))); sessions.insert(new RefreshSessionEntity(hash(refresh), account.id, now.plus(REFRESH_TTL))); return new SessionTokens(access, refresh, ACCESS_TTL.toSeconds()); }
    private AccessGrant access(String token) { var grant = accessGrants.get(token); if (grant == null || grant.expiresAt.isBefore(clock.instant())) { accessGrants.remove(token); throw new InvalidSessionException(); } return grant; }
    private void revoke(String accountId) { accessGrants.entrySet().removeIf(e -> e.getValue().account.id.equals(accountId)); sessions.delete(new LambdaQueryWrapper<RefreshSessionEntity>().eq(RefreshSessionEntity::getAccountId, accountId)); }
    private String encode(String password) { PasswordPolicy.validate(password); return passwords.encode(password); }
    private static String username(String value) { if (value == null || !value.trim().matches("[A-Za-z0-9_]{3,32}")) throw new ValidationException("username must be 3-32 letters, numbers, or underscores"); return value.trim().toLowerCase(); }
    private static String email(String value) { if (value == null || !value.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) throw new ValidationException("email must be valid"); return value.trim().toLowerCase(); }
    private static String token() { var bytes = new byte[32]; new SecureRandom().nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
    private static String hash(String token) { try { return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); } catch (Exception e) { throw new IllegalStateException(e); } }
    public record SessionTokens(String accessToken, String refreshToken, long expiresIn) {}
    private record AccessGrant(AccountEntity account, Instant expiresAt) {}
    public static final class ConflictException extends RuntimeException {} public static final class InvalidCredentialsException extends RuntimeException {} public static final class InvalidSessionException extends RuntimeException {} public static final class ValidationException extends RuntimeException { public ValidationException(String message) { super(message); } }
}
