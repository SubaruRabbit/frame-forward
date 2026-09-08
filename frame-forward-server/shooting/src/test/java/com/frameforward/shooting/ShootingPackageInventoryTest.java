package com.frameforward.shooting;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.frameforward.common.architecture.JavaLayerPackages;

class ShootingPackageInventoryTest {
    @Test
    void productionTypesUseTheirLayerDirectories() throws Exception {
        assertThat(JavaLayerPackages.inspect(Path.of("src/main/java"), "com.frameforward.shooting")).isEmpty();
    }
}
