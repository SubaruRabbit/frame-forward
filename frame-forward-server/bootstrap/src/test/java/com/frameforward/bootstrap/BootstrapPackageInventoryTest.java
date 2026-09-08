package com.frameforward.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.frameforward.common.architecture.JavaLayerPackages;

class BootstrapPackageInventoryTest {
    @Test
    void onlyApplicationEntryRemainsInRootPackage() throws Exception {
        assertThat(JavaLayerPackages.inspect(Path.of("src/main/java"), "com.frameforward.bootstrap")).singleElement()
                .satisfies(violation -> {
                    assertThat(violation.kind()).isEqualTo(JavaLayerPackages.Kind.ROOT_PACKAGE);
                    assertThat(violation.file())
                            .isEqualTo(Path.of("com/frameforward/bootstrap/FrameForwardApplication.java"));
                    assertThat(violation.actualPackage()).isEqualTo("com.frameforward.bootstrap");
                });
        assertThat(Path.of("src/main/java/com/frameforward/bootstrap/config/AccountDeletionConfiguration.java"))
                .isRegularFile();
        assertThat(Path.of("src/main/java/com/frameforward/bootstrap/repository/AccountDatabaseCleanup.java"))
                .isRegularFile();
    }
}
