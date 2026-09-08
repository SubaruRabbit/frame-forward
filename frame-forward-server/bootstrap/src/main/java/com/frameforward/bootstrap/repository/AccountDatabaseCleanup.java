package com.frameforward.bootstrap.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import com.frameforward.auth.gateway.AccountDataCleanup;

/** 清除没有账户外键的历史用户数据，避免迁移旧表时遗漏。 */
public class AccountDatabaseCleanup implements AccountDataCleanup {
    private final JdbcTemplate jdbc;
    public AccountDatabaseCleanup(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    @Override
    public void deleteForAccount(String accountId) {
        jdbc.update("DELETE FROM lesson_assignment_feedback WHERE account_id = ?", accountId);
        jdbc.update("DELETE FROM lesson_progress WHERE account_id = ?", accountId);
        jdbc.update("DELETE FROM user_equipment WHERE account_id = ?", accountId);
    }
}
