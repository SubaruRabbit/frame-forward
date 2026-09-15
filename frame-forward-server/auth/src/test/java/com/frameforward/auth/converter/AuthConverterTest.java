package com.frameforward.auth.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.frameforward.auth.model.entity.AccountDeletionJobEntity;
import com.frameforward.auth.model.entity.AccountEntity;
import com.frameforward.auth.model.entity.RefreshSessionEntity;

class AuthConverterTest {

	private final AuthConverter converter = Mappers.getMapper(AuthConverter.class);

	@Test
	void sensitiveEntitiesSupportConstructionWithoutRenderingSecrets() {
		var account = AccountEntity.builder().id("account").username("user").email("user@example.com")
				.passwordHash("password-secret").build();
		var session = RefreshSessionEntity.builder().tokenHash("session-secret").accountId("account")
				.expiresAt(Instant.parse("2026-01-01T00:00:00Z")).build();
		var deletion = AccountDeletionJobEntity.builder().id("job").accountId("account")
				.deletionTokenHash("deletion-secret").state("FAILED").failureReason("retry")
				.deletionTokenExpiresAt(Instant.parse("2026-01-02T00:00:00Z")).build();

		assertThat(account.toString()).doesNotContain("password-secret");
		assertThat(session.toString()).doesNotContain("session-secret");
		assertThat(deletion.toString()).doesNotContain("deletion-secret");
		assertThat(account.getUsername()).isEqualTo("user");
		assertThat(session.getAccountId()).isEqualTo("account");
		assertThat(deletion.getState()).isEqualTo("FAILED");
	}

	@Test
	void convertsDeletionJobFieldByFieldAndUsesOnlyTheProvidedRawToken() {
		var expiresAt = Instant.parse("2026-01-02T00:00:00Z");
		var entity = AccountDeletionJobEntity.builder().id("job").accountId("account").deletionTokenHash("hashed-token")
				.state("PENDING").failureReason(null).deletionTokenExpiresAt(expiresAt).build();

		var response = converter.toDeletionJob(entity, "raw-token");

		assertThat(response.jobId()).isEqualTo("job");
		assertThat(response.state()).isEqualTo("PENDING");
		assertThat(response.failureReason()).isNull();
		assertThat(response.deletionToken()).isEqualTo("raw-token");
		assertThat(response.deletionTokenExpiresAt()).isEqualTo(expiresAt);
	}

}
