package com.frameforward.ai.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class AiTaskEntityDocumentationTest {

	@Test
	void documentsEveryEntityFieldInChineseOnItsOwnLine() throws IOException {
		List<String> fields = List.of("id", "accountId", "operationType", "idempotencyKey", "state", "workflowVersion",
				"modelId", "promptVersion", "ruleVersion", "schemaVersion", "inputJson", "resultJson", "errorCode",
				"createdAt", "updatedAt");
		String source = Files
				.readString(Path.of("src/main/java", AiTaskEntity.class.getName().replace('.', '/') + ".java"));
		assertThat(AiTaskEntity.class.getDeclaredFields()).extracting("name").containsExactlyElementsOf(fields);
		assertThat(source).doesNotMatch("(?s).*public\\s+[^;\\n]+,[^;\\n]+;.*");

		for (String field : fields) {
			Pattern documentedField = Pattern.compile("(?s)/\\*\\*(?:(?!\\*/).)*[\\u4e00-\\u9fff]"
					+ "(?:(?!\\*/).)*\\*/\\s*(?:@\\w+(?:\\([^)]*\\))?\\s*)*public\\s+[^;\\n]+\\s+"
					+ Pattern.quote(field) + "\\s*;");
			assertThat(source).as("AiTaskEntity.%s 应有紧邻的中文 Javadoc", field)
					.matches(value -> documentedField.matcher(value).find());
		}
	}

}
