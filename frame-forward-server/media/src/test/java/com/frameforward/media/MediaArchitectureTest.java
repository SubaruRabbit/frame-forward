package com.frameforward.media;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.frameforward.media.mapper.MediaMapper;
import com.frameforward.media.service.MediaService;
import com.frameforward.media.service.PortfolioMediaQuery;

class MediaArchitectureTest {

    @Test
    void serviceAndQueryDependOnManagerInsteadOfMapper() {
        assertNoMapperDependency(MediaService.class);
        assertNoMapperDependency(PortfolioMediaQuery.class);
    }

    private static void assertNoMapperDependency(Class<?> type) {
        assertFalse(Arrays.stream(type.getDeclaredFields()).map(field -> field.getType())
                .anyMatch(MediaMapper.class::equals), () -> type.getSimpleName() + " must depend on MediaManager");
    }
}
