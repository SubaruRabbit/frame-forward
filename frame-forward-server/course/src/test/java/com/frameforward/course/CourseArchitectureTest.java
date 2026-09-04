package com.frameforward.course;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

class CourseArchitectureTest {

    @Test
    void serviceAndBusinessDependOnLayerAbstractionsInsteadOfMappers() {
        assertNoMapperDependency(CourseService.class);
        assertNoMapperDependency(CourseBusiness.class);
        assertTrue(Arrays.stream(CourseBusiness.class.getDeclaredFields())
                .anyMatch(field -> field.getType().equals(CourseManager.class)));
    }

    private static void assertNoMapperDependency(Class<?> type) {
        assertFalse(Arrays.stream(type.getDeclaredFields()).map(field -> field.getType()).anyMatch(
                BaseMapper.class::isAssignableFrom), () -> type.getSimpleName() + " must not depend on Mapper");
    }
}
