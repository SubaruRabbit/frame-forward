package com.frameforward.equipment;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frameforward.equipment.business.UserEquipmentBusiness;
import com.frameforward.equipment.manager.EquipmentManager;
import com.frameforward.equipment.service.CatalogService;
import com.frameforward.equipment.service.UserEquipmentService;

class EquipmentArchitectureTest {
    @Test
    void serviceAndBusinessDoNotDependOnMappers() {
        assertNoMapperDependency(CatalogService.class);
        assertNoMapperDependency(UserEquipmentService.class);
        assertNoMapperDependency(EquipmentManager.class);
        assertNoMapperDependency(UserEquipmentBusiness.class);
        assertTrue(Arrays.stream(UserEquipmentService.class.getDeclaredFields())
                .anyMatch(field -> field.getType() == UserEquipmentBusiness.class));
        assertTrue(Arrays.stream(UserEquipmentBusiness.class.getDeclaredFields())
                .anyMatch(field -> field.getType() == EquipmentManager.class));
    }
    private static void assertNoMapperDependency(Class<?> type) {
        assertFalse(Arrays.stream(type.getDeclaredFields()).map(field -> field.getType())
                .anyMatch(BaseMapper.class::isAssignableFrom));
    }
}
