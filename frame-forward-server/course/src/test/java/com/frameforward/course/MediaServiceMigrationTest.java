package com.frameforward.course;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.frameforward.course.service.CourseService;
class MediaServiceMigrationTest {
    @Test
    void usesCanonicalMediaService() {
        assertThat(Arrays.stream(CourseService.class.getDeclaredFields()).map(field -> field.getType().getName()))
                .contains("com.frameforward.media.service.MediaService")
                .doesNotContain("com.frameforward.media.MediaService");
    }
}
