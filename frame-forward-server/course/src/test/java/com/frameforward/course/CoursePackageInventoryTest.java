package com.frameforward.course;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.frameforward.common.architecture.JavaLayerPackages;
class CoursePackageInventoryTest {
    @Test
    void productionTypesFollowLayerPackages() throws Exception {
        assertThat(JavaLayerPackages.inspect(Path.of("src/main/java"), "com.frameforward.course")).isEmpty();
    }
    @Test
    void managerOnlyDependsOnRepositoryForPersistence() throws Exception {
        Class<?> manager = Class.forName("com.frameforward.course.manager.CourseManager");
        assertThat(Arrays.stream(manager.getDeclaredFields()).map(field -> field.getType().getName()))
                .contains("com.frameforward.course.repository.CourseRepository")
                .noneMatch(name -> name.endsWith("Mapper"));
    }
}
