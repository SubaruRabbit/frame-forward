package com.frameforward.media;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
class MediaServiceMigrationTest {
    @Test
    void serviceAndTransferRecordsHaveDedicatedDirectories() throws Exception {
        assertThat(Path.of("src/main/java/com/frameforward/media/service/MediaService.java")).isRegularFile();
        Class<?> service = Class.forName("com.frameforward.media.service.MediaService");
        assertThat(service.getDeclaredClasses()).noneMatch(Class::isRecord);
        for (String dto : List.of("MediaResponse", "UploadProgress")) {
            assertThat(Class.forName("com.frameforward.media.model.dto." + dto).isRecord()).isTrue();
        }
    }
}
