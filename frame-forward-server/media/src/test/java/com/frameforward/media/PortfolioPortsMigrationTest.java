package com.frameforward.media;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
class PortfolioPortsMigrationTest {
    @Test
    void portfolioTypesUseTheirCanonicalPackages() throws Exception {
        Path root = Path.of("src/main/java/com/frameforward/media");
        for (String name : List.of("service/PortfolioMediaQuery", "model/dto/PortfolioMediaItem",
                "gateway/WorkMediaCleanup")) {
            assertThat(root.resolve(name + ".java")).isRegularFile();
        }
        assertThat(root.resolve("PortfolioMediaQuery.java")).doesNotExist();
        assertThat(root.resolve("WorkMediaCleanup.java")).doesNotExist();
        assertThat(Class.forName("com.frameforward.media.service.PortfolioMediaQuery").getDeclaredClasses())
                .noneMatch(Class::isRecord);
    }
}
