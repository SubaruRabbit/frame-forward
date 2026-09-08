package com.frameforward.evaluation;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import com.frameforward.evaluation.service.PhotoEvaluationService;
import com.frameforward.evaluation.service.RetakeComparisonService;

class EvaluationArchitectureTest {
    @Test
    void servicesDependOnLayerAbstractionsInsteadOfMappers() {
        assertNoMapperDependency(PhotoEvaluationService.class);
        assertNoMapperDependency(RetakeComparisonService.class);
    }
    private static void assertNoMapperDependency(Class<?> type) {
        assertThat(type.getDeclaredFields()).extracting(Field::getType)
                .noneMatch(fieldType -> fieldType.getPackageName().equals("com.frameforward.evaluation")
                        && fieldType.getSimpleName().endsWith("Mapper"));
    }
}
