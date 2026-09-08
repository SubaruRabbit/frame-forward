package com.frameforward.equipment;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.frameforward.common.architecture.JavaLayerPackages;

class EquipmentPackageInventoryTest {
    @Test
    void allProductionClassesUseLayerDirectories() throws Exception {
        assertThat(JavaLayerPackages.inspect(Path.of("src/main/java"), "com.frameforward.equipment")).isEmpty();
    }

    @Test
    void controllersAndServicesDoNotOwnTransferRecords() throws Exception {
        for (String name : List.of("controller.CatalogController", "controller.UserEquipmentController",
                "service.CatalogService", "service.UserEquipmentService")) {
            assertThat(Class.forName("com.frameforward.equipment." + name).getDeclaredClasses())
                    .noneMatch(Class::isRecord);
        }
    }

    @Test
    void businessAndManagerRespectDependencyDirection() throws Exception {
        Path root = Path.of("src/main/java/com/frameforward/equipment");
        for (String layer : List.of("business", "manager")) {
            assertThat(root.resolve(layer)).isDirectory();
            try (var files = Files.walk(root.resolve(layer))) {
                for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                    assertThat(Files.readString(file)).doesNotContain("com.baomidou", "equipment.mapper",
                            "equipment.service", "equipment.controller");
                }
            }
        }
    }
}
