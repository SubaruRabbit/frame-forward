package com.frameforward.auth.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class AuthEntityDocumentationTest {

	private static final Pattern CHINESE = Pattern.compile("[\\u4e00-\\u9fff]");

	@Test
	void documentsEveryEntityFieldInChineseOnItsOwnLine() throws IOException {
		assertDocumented(AccountEntity.class, List.of("id", "username", "email", "passwordHash"));
		assertDocumented(RefreshSessionEntity.class, List.of("tokenHash", "accountId", "expiresAt"));
		assertDocumented(AccountDeletionJobEntity.class, List.of("id", "accountId", "deletionTokenHash", "state",
				"failureReason", "deletionTokenExpiresAt", "createdAt", "updatedAt"));
	}

	private void assertDocumented(Class<?> type, List<String> expectedFields) throws IOException {
		Path sourcePath = Path.of("src/main/java", type.getName().replace('.', '/') + ".java");
		String source = Files.readString(sourcePath);
		assertThat(type.getDeclaredFields()).extracting("name").containsExactlyElementsOf(expectedFields);
		assertThat(source).doesNotMatch("(?s).*public\\s+[^;\\n]+,[^;\\n]+;.*");

		for (String field : expectedFields) {
			Pattern documentedField = Pattern.compile("(?s)/\\*\\*(?:(?!\\*/).)*[\\u4e00-\\u9fff]"
					+ "(?:(?!\\*/).)*\\*/\\s*(?:@\\w+(?:\\([^)]*\\))?\\s*)*public\\s+[^;\\n]+\\s+"
					+ Pattern.quote(field) + "\\s*;");
			assertThat(source).as("%s.%s 应有紧邻的中文 Javadoc", type.getSimpleName(), field)
					.matches(value -> documentedField.matcher(value).find());
		}
	}

}
