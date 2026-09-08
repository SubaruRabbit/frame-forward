package com.frameforward.generation;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import com.frameforward.generation.business.ReferenceImageBusiness;
import com.frameforward.generation.manager.ReferenceImageManager;
import com.frameforward.generation.service.ReferenceImageCompletionProcessor;
import com.frameforward.generation.service.ReferenceImageService;

class ReferenceImageArchitectureTest {
    @Test
    void serviceAndCompletionProcessorDependOnLayerAbstractionsInsteadOfMappers() {
        assertNoMapperDependency(ReferenceImageService.class);
        assertNoMapperDependency(ReferenceImageCompletionProcessor.class);
        assertThat(ReferenceImageBusiness.class.getDeclaredFields()).extracting(Field::getType)
                .contains(ReferenceImageManager.class);
    }

    private static void assertNoMapperDependency(Class<?> type) {
        assertThat(type.getDeclaredFields()).extracting(Field::getType)
                .noneMatch(fieldType -> fieldType.getSimpleName().endsWith("Mapper"));
    }
}
