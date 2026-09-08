package com.frameforward.media.service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.frameforward.auth.gateway.AccountDataCleanup;
import com.frameforward.media.gateway.WorkMediaCleanup;
import com.frameforward.media.manager.MediaManager;
class MediaServiceRegistrationTest {
    @Test
    void canonicalServiceIsTheOnlyBeanForBothCleanupContracts() throws Exception {
        assertThat(Path.of("src/main/java/com/frameforward/media/MediaService.java")).doesNotExist();
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(MediaManager.class, () -> mock(MediaManager.class));
            context.registerBean(com.fasterxml.jackson.databind.ObjectMapper.class,
                    () -> new com.fasterxml.jackson.databind.ObjectMapper());
            context.scan("com.frameforward.media.service");
            context.refresh();
            assertThat(context.getBeansOfType(MediaService.class)).hasSize(1);
            var service = context.getBean(MediaService.class);
            assertThat(context.getBean(AccountDataCleanup.class)).isSameAs(service);
            assertThat(context.getBean(WorkMediaCleanup.class)).isSameAs(service);
        }
        assertThat(MediaService.class.isAnnotationPresent(Primary.class)).isTrue();
        assertThat(MediaService.class.getMethod("ingest", String.class, MultipartFile.class)
                .isAnnotationPresent(Transactional.class)).isTrue();
        assertThat(MediaService.class.getMethod("registerGenerated", String.class, String.class, int.class, int.class)
                .isAnnotationPresent(Transactional.class)).isTrue();
    }
}
