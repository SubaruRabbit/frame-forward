package com.frameforward.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class MediaEntityMigrationTest {
    @Test
    void servicesUseCanonicalMediaEntityQueries() throws Exception {
        var root = Path.of("src/main/java/com/frameforward/evaluation");
        assertThat(Files.readString(root.resolve("service/PhotoEvaluationService.java")))
                .contains("import com.frameforward.media.model.entity.MediaEntity;", ".findOwnedEntity(");
        assertThat(Files.readString(root.resolve("service/RetakeComparisonService.java")))
                .contains("import com.frameforward.media.model.entity.MediaEntity;", ".findEntityById(");
    }
}
