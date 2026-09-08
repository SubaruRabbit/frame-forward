package com.frameforward.evaluation;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.frameforward.evaluation.repository.ShootingSessionRepository;
class ShootingPlanMigrationTest {
    @Test
    void repositoryUsesCanonicalPlanMapperAndEntity() throws Exception {
        assertThat(ShootingSessionRepository.class.getDeclaredField("plans").getType().getName())
                .isEqualTo("com.frameforward.shooting.mapper.ShootingPlanMapper");
        assertThat(ShootingSessionRepository.class.getMethod("findOwnedPlan", String.class, String.class)
                .getReturnType().getName()).isEqualTo("com.frameforward.shooting.model.entity.ShootingPlanEntity");
    }
}
