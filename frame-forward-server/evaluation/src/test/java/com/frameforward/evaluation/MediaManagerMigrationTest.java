package com.frameforward.evaluation;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.frameforward.evaluation.service.PhotoEvaluationService;
import com.frameforward.evaluation.service.RetakeComparisonService;
class MediaManagerMigrationTest {
    @Test
    void usesCanonicalMediaManager() {
        for (Class<?> type : List.of(PhotoEvaluationService.class, RetakeComparisonService.class)) {
            assertThat(Arrays.stream(type.getDeclaredFields()).map(field -> field.getType().getName()))
                    .contains("com.frameforward.media.manager.MediaManager")
                    .doesNotContain("com.frameforward.media.MediaManager");
        }
    }
}
