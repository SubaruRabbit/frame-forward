package com.frameforward.evaluation;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
class UnitTestLayerMigrationTest {
    @Test
    void singleSubjectTestsMirrorProductionLayers() throws Exception {
        for (String target : new String[]{"service.PhotoEvaluationServiceTest", "service.PortfolioEvaluationQueryTest",
                "service.RetakeComparisonServiceTest"}) {
            assertThat(Class.forName("com.frameforward.evaluation." + target).getPackageName())
                    .isEqualTo("com.frameforward.evaluation." + target.substring(0, target.lastIndexOf('.')));
            assertThat(Files.exists(Path.of("src/test/java/com/frameforward/evaluation/"
                    + target.substring(target.lastIndexOf('.') + 1) + ".java"))).isFalse();
        }
    }
}
