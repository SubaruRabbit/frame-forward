package com.frameforward.shooting;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frameforward.shooting.business.ShootingBusiness;
import com.frameforward.shooting.manager.ShootingManager;
import com.frameforward.shooting.service.SceneAnalysisService;
import com.frameforward.shooting.service.ShootingPlanService;

class ShootingArchitectureTest {

    @Test
    void servicesAndBusinessDependOnLayerAbstractionsInsteadOfMappers() {
        assertNoMapperDependency(SceneAnalysisService.class);
        assertNoMapperDependency(ShootingPlanService.class);
        assertNoMapperDependency(ShootingBusiness.class);
        assertNoMapperDependency(ShootingManager.class);
        assertTrue(Arrays.stream(ShootingBusiness.class.getDeclaredFields())
                .anyMatch(field -> field.getType().equals(ShootingManager.class)));
    }

    private static void assertNoMapperDependency(Class<?> type) {
        assertFalse(Arrays.stream(type.getDeclaredFields()).map(field -> field.getType()).anyMatch(
                BaseMapper.class::isAssignableFrom), () -> type.getSimpleName() + " must not depend on Mapper");
    }
}
