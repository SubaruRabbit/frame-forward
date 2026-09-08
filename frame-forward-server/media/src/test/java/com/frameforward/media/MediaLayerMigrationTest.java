package com.frameforward.media;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class MediaLayerMigrationTest {
    @Test
    void foundationTypesOccupyTheirLayerDirectories() throws Exception {
        Path root = Path.of("src/main/java/com/frameforward/media");
        for (String name : List.of("controller/MediaController", "controller/MediaExceptionHandler",
                "mapper/MediaMapper", "manager/MediaManager", "repository/MediaRepository")) {
            Path file = root.resolve(name + ".java");
            assertThat(file).isRegularFile();
            assertThat(Files.readString(file))
                    .contains("package com.frameforward.media." + name.substring(0, name.indexOf('/')) + ";");
        }
        assertThat(Files.readString(root.resolve("manager/MediaManager.java"))).doesNotContain("com.baomidou",
                "media.mapper", "media.service", "media.controller");
    }
}
