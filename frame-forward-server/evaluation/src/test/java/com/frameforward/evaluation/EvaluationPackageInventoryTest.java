package com.frameforward.evaluation;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.frameforward.common.architecture.JavaLayerPackages;
class EvaluationPackageInventoryTest {
    @Test
    void productionTypesUseLayerPackages() throws Exception {
        assertThat(JavaLayerPackages.inspect(Path.of("src/main/java"), "com.frameforward.evaluation")).isEmpty();
    }
    @Test
    void servicesAndManagersDoNotDependOnMappers() throws Exception {
        for (var name : List.of("service.PhotoEvaluationService", "service.RetakeComparisonService",
                "service.ShootingSessionService", "service.PortfolioEvaluationQuery", "service.EvaluationWorkCleanup",
                "manager.EvaluationManager", "manager.ShootingSessionManager")) {
            var type = Class.forName("com.frameforward.evaluation." + name);
            assertThat(Arrays.stream(type.getDeclaredFields()).map(field -> field.getType()))
                    .noneMatch(com.baomidou.mybatisplus.core.mapper.BaseMapper.class::isAssignableFrom);
        }
    }
}
