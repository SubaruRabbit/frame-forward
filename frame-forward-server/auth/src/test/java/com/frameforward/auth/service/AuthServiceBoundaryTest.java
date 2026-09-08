package com.frameforward.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.frameforward.auth.business.AuthBusiness;
import com.frameforward.auth.manager.AuthManager;
import com.frameforward.auth.model.entity.AccountDeletionJobEntity;
import com.frameforward.auth.model.entity.AccountEntity;
import com.frameforward.auth.model.entity.RefreshSessionEntity;

class AuthServiceBoundaryTest {
    private final AuthManager manager = mock(AuthManager.class);
    private final AuthBusiness business = mock(AuthBusiness.class);
    private final AuthService auth = new AuthService(manager, business);
    private final AccountEntity account = new AccountEntity("account", "user", "user@example.com",
            new BCryptPasswordEncoder().encode("ValidPass1!"));

    @BeforeEach
    void arrangeAccount() {
        when(manager.findByIdentifier(anyString())).thenReturn(account);
        when(manager.findAccount(account.id)).thenReturn(account);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Basic abc", "bearer abc", "Bearer"})
    void rejectsInvalidAuthorizationScheme(String header) {
        assertThrows(AuthService.InvalidSessionException.class, () -> AuthService.bearer(header));
    }

    @Test
    void preservesBearerTokenWithoutTrimming() {
        assertEquals("abc ", AuthService.bearer("Bearer abc "));
        assertEquals("", AuthService.bearer("Bearer "));
    }

    @Test
    void loginNormalizesIdentifierAndLogoutRevokesOnlyThatAccount() {
        var session = auth.login(" USER ", "ValidPass1!");
        verify(manager).findByIdentifier("user");
        assertEquals(account.id, auth.requireAccountId(session.accessToken()));
        auth.logout(session.accessToken());
        verify(manager).deleteSessionsForAccount(account.id);
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.requireAccountId(session.accessToken()));
    }

    @Test
    void rejectsMissingAccountDeletionAndWrongPassword() {
        when(manager.findByIdentifier("")).thenReturn(null);
        assertThrows(AuthService.InvalidCredentialsException.class, () -> auth.login(null, "ValidPass1!"));
        assertThrows(AuthService.InvalidCredentialsException.class, () -> auth.login("user", null));
        when(manager.hasUnfinishedDeletion(account.id)).thenReturn(true);
        assertThrows(AuthService.InvalidCredentialsException.class, () -> auth.login("user", "ValidPass1!"));
    }

    @Test
    void refreshConsumesSessionBeforeIssuingReplacement() {
        when(manager.findSession(anyString())).thenReturn(new RefreshSessionEntity("hash", account.id, Instant.MAX));
        when(manager.deleteSession(anyString())).thenReturn(1);
        var session = auth.refresh("refresh");
        assertEquals(account.id, auth.requireAccountId(session.accessToken()));
        verify(manager).deleteSession(anyString());
        verify(manager).insertSession(any());
    }

    @Test
    void rejectsMissingExpiredOrAlreadyConsumedRefreshSession() {
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.refresh("missing"));
        when(manager.findSession(anyString())).thenReturn(new RefreshSessionEntity("hash", account.id, Instant.EPOCH));
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.refresh("expired"));
        when(manager.findSession(anyString())).thenReturn(new RefreshSessionEntity("hash", account.id, Instant.MAX));
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.refresh("consumed"));
        verify(manager, never()).insertSession(any());
    }

    @Test
    void rejectsRefreshForMissingOrDeletingAccount() {
        when(manager.findSession(anyString())).thenReturn(new RefreshSessionEntity("hash", account.id, Instant.MAX));
        when(manager.deleteSession(anyString())).thenReturn(1);
        when(manager.findAccount(account.id)).thenReturn(null);
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.refresh("refresh"));
        when(manager.findAccount(account.id)).thenReturn(account);
        when(manager.hasUnfinishedDeletion(account.id)).thenReturn(true);
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.refresh("refresh"));
    }

    @Test
    void passwordChangeValidatesCurrentPasswordAndRevokesSession() {
        var session = auth.login("user", "ValidPass1!");
        assertThrows(AuthService.InvalidCredentialsException.class,
                () -> auth.changePassword(session.accessToken(), null, "NewPass12!"));
        auth.changePassword(session.accessToken(), "ValidPass1!", "NewPass12!");
        verify(manager).updateAccount(account);
        assertTrue(new BCryptPasswordEncoder().matches("NewPass12!", account.passwordHash));
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.requireAccountId(session.accessToken()));
    }

    @Test
    void registrationPreservesValidationAndConflictErrors() {
        assertThrows(AuthService.ValidationException.class,
                () -> auth.register(null, "user@example.com", "ValidPass1!"));
        assertThrows(AuthService.ValidationException.class,
                () -> auth.register("?", "user@example.com", "ValidPass1!"));
        assertThrows(AuthService.ValidationException.class, () -> auth.register("user", null, "ValidPass1!"));
        assertThrows(AuthService.ValidationException.class, () -> auth.register("user", "invalid", "ValidPass1!"));
        assertThrows(AuthService.ValidationException.class, () -> auth.register("user", "user@example.com", "weak"));
        doThrow(new DuplicateKeyException("duplicate")).when(manager).insertAccount(any());
        assertThrows(AuthService.ConflictException.class,
                () -> auth.register("user", "user@example.com", "ValidPass1!"));
    }

    @Test
    void deletionRequiresCurrentPasswordAndAnActiveAccount() {
        var session = auth.login("user", "ValidPass1!");
        assertThrows(AuthService.InvalidCredentialsException.class,
                () -> auth.startAccountDeletion(session.accessToken(), null));
        when(manager.hasUnfinishedDeletion(account.id)).thenReturn(false, true);
        assertThrows(AuthService.InvalidSessionException.class,
                () -> auth.startAccountDeletion(session.accessToken(), "ValidPass1!"));
        verify(manager, never()).insertDeletionJob(any());
    }

    @Test
    void deletionTokenChecksExpiryOwnershipAndCompletedRetry() throws Exception {
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.deletionStatus("job", "token"));
        var job = new AccountDeletionJobEntity();
        job.id = "job";
        job.state = "COMPLETED";
        job.deletionTokenExpiresAt = Instant.MAX;
        job.deletionTokenHash = Base64.getEncoder()
                .encodeToString(MessageDigest.getInstance("SHA-256").digest("token".getBytes(StandardCharsets.UTF_8)));
        when(manager.findDeletionJob("job")).thenReturn(job);
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.deletionStatus("job", null));
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.deletionStatus("job", " "));
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.deletionStatus("job", "wrong"));
        assertNull(auth.deletionStatus("job", "token").deletionToken());
        assertEquals("COMPLETED", auth.retryAccountDeletion("job", "token").state());
        verifyNoInteractions(business);
        job.deletionTokenExpiresAt = Instant.EPOCH;
        assertThrows(AuthService.InvalidSessionException.class, () -> auth.deletionStatus("job", "token"));
    }
}
