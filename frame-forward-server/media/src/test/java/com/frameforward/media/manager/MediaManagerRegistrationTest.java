package com.frameforward.media.manager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.frameforward.media.repository.MediaRepository;
class MediaManagerRegistrationTest {
    @Test
    void canonicalManagerIsTheOnlyScannedImplementation() {
        assertThat(Path.of("src/main/java/com/frameforward/media/MediaManager.java")).doesNotExist();
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(MediaRepository.class, () -> mock(MediaRepository.class));
            context.scan("com.frameforward.media.manager");
            context.refresh();
            assertThat(context.getBeansOfType(MediaManager.class)).hasSize(1);
            assertThat(context.getBean(MediaManager.class).getClass()).isEqualTo(MediaManager.class);
        }
    }
}
