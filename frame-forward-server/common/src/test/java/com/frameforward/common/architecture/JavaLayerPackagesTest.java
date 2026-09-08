package com.frameforward.common.architecture;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JavaLayerPackagesTest {
    @TempDir
    Path root;

    @Test
    void acceptsMatchingLayerDirectories() throws IOException {
        for (String role : new String[]{"controller/OrderController", "service/OrderService", "business/OrderBusiness",
                "manager/OrderManager", "mapper/OrderMapper", "model/entity/OrderEntity"}) {
            int split = role.lastIndexOf('/');
            source("com/example/" + role + ".java", "package com.example." + role.substring(0, split).replace('/', '.')
                    + "; class " + role.substring(split + 1) + " {}");
        }
        assertTrue(JavaLayerPackages.inspect(root, "com.example").isEmpty());
    }

    @Test
    void detectsDirectoryPackageMismatch() throws IOException {
        source("com/example/mapper/OrderMapper.java", "package com.example.service; interface OrderMapper {}");
        assertEquals(Set.of("DIRECTORY_PACKAGE_MISMATCH", "ROLE_PACKAGE_MISMATCH"), kinds());
    }

    @Test
    void detectsRootPackageEntity() throws IOException {
        source("com/example/OrderEntity.java", "package com.example; class OrderEntity {}");
        var violations = JavaLayerPackages.inspect(root, "com.example");
        assertEquals(Set.of("ROOT_PACKAGE", "ROLE_PACKAGE_MISMATCH"), kinds());
        assertTrue(violations.stream().anyMatch(v -> v.expectedPackage().equals("com.example.model.entity")));
    }

    @Test
    void rejectsMapperInServiceEvenWhenDirectoryMatches() throws IOException {
        source("com/example/service/OrderMapper.java", "package com.example.service; interface OrderMapper {}");
        assertEquals(Set.of("ROLE_PACKAGE_MISMATCH"), kinds());
    }

    @Test
    void readsRealPackageRatherThanCommentOrString() throws IOException {
        source("com/example/service/OrderService.java", """
                // package wrong.comment;
                package com.example.service;
                class OrderService { String example = "package wrong.string;"; }
                """);
        assertTrue(JavaLayerPackages.inspect(root, "com.example").isEmpty());
    }

    @Test
    void failsForMissingRootAndMalformedSource() throws IOException {
        assertThrows(IOException.class, () -> JavaLayerPackages.inspect(root.resolve("missing"), "com.example"));
        source("Broken.java", "class {");
        assertThrows(IOException.class, () -> JavaLayerPackages.inspect(root, "com.example"));
    }

    @Test
    void reportsMissingOrForeignPackage() throws IOException {
        source("NoPackage.java", "class NoPackage {}");
        source("other/Foreign.java", "package other; class Foreign {}");
        assertEquals(Set.of("MISSING_PACKAGE", "OUTSIDE_BASE_PACKAGE"), kinds());
    }

    private Set<String> kinds() throws IOException {
        return JavaLayerPackages.inspect(root, "com.example").stream().map(v -> v.kind().name())
                .collect(Collectors.toSet());
    }

    private void source(String relative, String content) throws IOException {
        Path path = root.resolve(relative);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }
}
