package com.frameforward.portfolio;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class UnitTestLayerMigrationTest {
    @Test
    void singleSubjectTestsMirrorProductionLayers() throws Exception {
        for (String target : new String[]{"business.PortfolioBusinessTest", "manager.PortfolioManagerTest",
                "service.PortfolioServiceTest"}) {
            assertThat(Class.forName("com.frameforward.portfolio." + target).getPackageName())
                    .isEqualTo("com.frameforward.portfolio." + target.substring(0, target.lastIndexOf('.')));
            assertThat(Files.exists(Path.of("src/test/java/com/frameforward/portfolio/"
                    + target.substring(target.lastIndexOf('.') + 1) + ".java"))).isFalse();
        }
    }
}
