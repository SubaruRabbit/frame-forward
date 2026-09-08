package com.frameforward.generation;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
class MediaEntityMigrationTest {
    @Test
    void usesCanonicalMediaEntityAndQuery() throws Exception {
        String source = Files
                .readString(Path.of("src/main/java/com/frameforward/generation/service/ReferenceImageService.java"));
        assertThat(source).contains("import com.frameforward.media.model.entity.MediaEntity;", ".findOwnedEntity(")
                .doesNotContain("import com.frameforward.media.MediaEntity;", ".findOwned(");
    }
}
