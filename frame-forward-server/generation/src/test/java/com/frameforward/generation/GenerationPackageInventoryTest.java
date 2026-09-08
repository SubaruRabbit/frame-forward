package com.frameforward.generation;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.frameforward.common.architecture.JavaLayerPackages;
class GenerationPackageInventoryTest {
    @Test
    void allProductionTypesFollowLayerDirectories() throws Exception {
        assertThat(JavaLayerPackages.inspect(Path.of("src/main/java"), "com.frameforward.generation")).isEmpty();
    }
    @Test
    void managerDoesNotDependOnServiceOrMapper() throws Exception {
        Class<?> manager = Class.forName("com.frameforward.generation.manager.ReferenceImageManager");
        assertThat(Arrays.stream(manager.getDeclaredFields()).map(field -> field.getType().getName()))
                .contains("com.frameforward.generation.repository.ReferenceImageRepository")
                .noneMatch(name -> name.endsWith("Mapper") || name.endsWith("Service"));
    }
}
