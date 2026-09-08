package com.frameforward.portfolio;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frameforward.portfolio.business.PortfolioBusiness;
import com.frameforward.portfolio.manager.PortfolioManager;
import com.frameforward.portfolio.service.PortfolioService;

class PortfolioArchitectureTest {

    @Test
    void serviceAndBusinessDependOnLayerAbstractionsInsteadOfMappers() {
        assertNoMapperDependency(PortfolioService.class);
        assertNoMapperDependency(PortfolioBusiness.class);
        assertTrue(Arrays.stream(PortfolioBusiness.class.getDeclaredFields())
                .anyMatch(field -> field.getType().equals(PortfolioManager.class)));
    }

    private static void assertNoMapperDependency(Class<?> type) {
        assertFalse(Arrays.stream(type.getDeclaredFields()).map(field -> field.getType()).anyMatch(
                BaseMapper.class::isAssignableFrom), () -> type.getSimpleName() + " must not depend on Mapper");
    }
}
