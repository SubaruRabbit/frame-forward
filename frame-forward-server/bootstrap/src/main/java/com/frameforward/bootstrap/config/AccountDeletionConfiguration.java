package com.frameforward.bootstrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.frameforward.auth.gateway.AccountDataCleanup;
import com.frameforward.bootstrap.repository.AccountDatabaseCleanup;

@Configuration(proxyBeanMethods = false)
class AccountDeletionConfiguration {

    @Bean
    AccountDataCleanup accountDatabaseCleanup(JdbcTemplate jdbc) {
        return new AccountDatabaseCleanup(jdbc);
    }
}
