package com.frameforward.media;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.frameforward.common.architecture.JavaLayerPackages;
import com.frameforward.media.manager.MediaManager;

class MediaPackageInventoryTest {
    @Test
    void allProductionTypesUseLayerPackagesWithoutCompatibilityEntrypoints() throws Exception {
        assertThat(JavaLayerPackages.inspect(Path.of("src/main/java"), "com.frameforward.media")).isEmpty();
        assertThat(MediaManager.class.getDeclaredMethods()).extracting("name").doesNotContain("findOwned", "findById");
    }
}
