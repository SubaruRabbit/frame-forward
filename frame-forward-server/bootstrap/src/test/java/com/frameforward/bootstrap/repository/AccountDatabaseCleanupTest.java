package com.frameforward.bootstrap.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

class AccountDatabaseCleanupTest {
    @Test
    void deletesOnlyRequestedAccountInOriginalOrder() {
        var jdbc = mock(JdbcTemplate.class);
        new AccountDatabaseCleanup(jdbc).deleteForAccount("account");
        var order = inOrder(jdbc);
        order.verify(jdbc).update("DELETE FROM lesson_assignment_feedback WHERE account_id = ?", "account");
        order.verify(jdbc).update("DELETE FROM lesson_progress WHERE account_id = ?", "account");
        order.verify(jdbc).update("DELETE FROM user_equipment WHERE account_id = ?", "account");
        order.verifyNoMoreInteractions();
    }

    @Test
    void databaseFailurePropagatesWithoutContinuingCleanup() {
        var jdbc = mock(JdbcTemplate.class);
        var failure = new DataAccessResourceFailureException("unavailable");
        when(jdbc.update("DELETE FROM lesson_progress WHERE account_id = ?", "account")).thenThrow(failure);
        assertThatThrownBy(() -> new AccountDatabaseCleanup(jdbc).deleteForAccount("account")).isSameAs(failure);
        verify(jdbc).update("DELETE FROM lesson_assignment_feedback WHERE account_id = ?", "account");
        verify(jdbc).update("DELETE FROM lesson_progress WHERE account_id = ?", "account");
        verifyNoMoreInteractions(jdbc);
    }
}
