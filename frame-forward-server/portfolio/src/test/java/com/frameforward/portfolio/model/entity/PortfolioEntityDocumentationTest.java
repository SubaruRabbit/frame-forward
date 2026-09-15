package com.frameforward.portfolio.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class PortfolioEntityDocumentationTest {

	@Test
	void documentsEveryEntityFieldInChineseOnItsOwnLine() throws IOException {
		Map<Class<?>, List<String>> entities = Map.of(PortfolioFavoriteEntity.class,
				List.of("mediaId", "accountId", "createdAt"), PortfolioWorkDeletionJobEntity.class,
				List.of("id", "accountId", "mediaId", "state", "failureReason", "createdAt", "updatedAt"));

		for (var entry : entities.entrySet()) {
			assertDocumented(entry.getKey(), entry.getValue());
		}
	}

	private void assertDocumented(Class<?> type, List<String> fields) throws IOException {
		String source = Files.readString(Path.of("src/main/java", type.getName().replace('.', '/') + ".java"));
		assertThat(type.getDeclaredFields()).extracting("name").containsExactlyElementsOf(fields);
		assertThat(source).doesNotMatch("(?s).*public\\s+[^;\\n]+,[^;\\n]+;.*");

		for (String field : fields) {
			Pattern documentedField = Pattern.compile("(?s)/\\*\\*(?:(?!\\*/).)*[\\u4e00-\\u9fff]"
					+ "(?:(?!\\*/).)*\\*/\\s*(?:@\\w+(?:\\([^)]*\\))?\\s*)*public\\s+[^;\\n]+\\s+"
					+ Pattern.quote(field) + "\\s*;");
			assertThat(source).as("%s.%s 应有紧邻的中文 Javadoc", type.getSimpleName(), field)
					.matches(value -> documentedField.matcher(value).find());
		}
	}

}
