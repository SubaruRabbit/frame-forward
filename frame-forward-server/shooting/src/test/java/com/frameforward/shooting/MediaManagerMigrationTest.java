package com.frameforward.shooting;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.frameforward.shooting.service.SceneAnalysisService;
class MediaManagerMigrationTest {
    @Test
    void usesCanonicalMediaManager() {
        assertThat(
                Arrays.stream(SceneAnalysisService.class.getDeclaredFields()).map(field -> field.getType().getName()))
                .contains("com.frameforward.media.manager.MediaManager")
                .doesNotContain("com.frameforward.media.MediaManager");
    }
}
