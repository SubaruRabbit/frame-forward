package com.frameforward.generation;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.frameforward.generation.repository.ReferenceImageRepository;
class ShootingPlanMigrationTest {
    @Test
    void repositoryUsesCanonicalPlanMapperAndEntity() throws Exception {
        assertThat(ReferenceImageRepository.class.getDeclaredField("plans").getType().getName())
                .isEqualTo("com.frameforward.shooting.mapper.ShootingPlanMapper");
        assertThat(ReferenceImageRepository.class.getMethod("findOwnedPlan", String.class, String.class).getReturnType()
                .getName()).isEqualTo("com.frameforward.shooting.model.entity.ShootingPlanEntity");
    }
}
