package com.frameforward.portfolio;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.frameforward.common.architecture.JavaLayerPackages;
class PortfolioPackageInventoryTest {
    @Test
    void allProductionTypesUseLayerDirectories() throws Exception {
        assertThat(JavaLayerPackages.inspect(Path.of("src/main/java"), "com.frameforward.portfolio")).isEmpty();
    }
    @Test
    void managerUsesRepositoryAndFilterContainsNoNormalizationLogic() throws Exception {
        var manager = Class.forName("com.frameforward.portfolio.manager.PortfolioManager");
        assertThat(Arrays.stream(manager.getDeclaredFields()).map(field -> field.getType().getName()))
                .contains("com.frameforward.portfolio.repository.PortfolioRepository")
                .noneMatch(name -> name.endsWith("Mapper"));
        var filter = Class.forName("com.frameforward.portfolio.model.dto.Filter");
        assertThat(Arrays.stream(filter.getDeclaredMethods()).map(method -> method.getName()))
                .doesNotContain("normalized", "blank");
    }
}
