package com.frameforward.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

class AiInternalLayerMigrationTest {
    @Test
    void internalRulesAndPersistenceUseCanonicalLayers() throws Exception {
        for (String name : new String[]{"SceneAnalysisGraph", "PlanGenerationGraph", "PhotoEvaluationGraph",
                "ReferenceImageGraph", "CourseGenerationValidator"}) {
            assertThat(Class.forName("com.frameforward.ai.business." + name).getPackageName())
                    .isEqualTo("com.frameforward.ai.business");
            assertThat(Files.exists(Path.of("src/main/java/com/frameforward/ai/" + name + ".java"))).isFalse();
        }
        Class<?> manager = Class.forName("com.frameforward.ai.manager.AiTaskManager");
        assertThat(Arrays.stream(manager.getDeclaredFields()).map(f -> f.getType()))
                .noneMatch(BaseMapper.class::isAssignableFrom);
        assertThat(manager.getDeclaredField("tasks").getType().getName())
                .isEqualTo("com.frameforward.ai.repository.AiTaskRepository");
        assertThat(BaseMapper.class.isAssignableFrom(Class.forName("com.frameforward.ai.mapper.AiTaskMapper")))
                .isTrue();
    }
}
