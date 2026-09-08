package com.frameforward.ai;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.frameforward.ai.business.AiTaskBusiness;
import com.frameforward.ai.manager.AiTaskManager;
import com.frameforward.common.architecture.JavaLayerPackages;

class AiPackageInventoryTest {
    @Test
    void allProductionTypesUseCanonicalLayers() throws Exception {
        assertThat(JavaLayerPackages.inspect(Path.of("src/main/java"), "com.frameforward.ai")).isEmpty();
    }

    @Test
    void businessAndManagerRespectDependencyDirection() {
        assertThat(Arrays.stream(AiTaskBusiness.class.getDeclaredFields())
                .map(field -> field.getGenericType().getTypeName())).noneMatch(
                        type -> type.contains(".gateway.") || type.contains(".service.") || type.contains(".mapper."));
        assertThat(Arrays.stream(AiTaskManager.class.getDeclaredFields())
                .map(field -> field.getGenericType().getTypeName()))
                .noneMatch(type -> type.contains(".business.") || type.contains(".service.")
                        || type.contains(".mapper.") || type.contains("mybatis"));
    }
}
