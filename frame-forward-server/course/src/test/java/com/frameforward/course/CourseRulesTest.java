package com.frameforward.course;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class CourseRulesTest {
  @Test void regenerationCreatesNewVersionWhenLearningHasStarted() {
    var original = new CourseVersion("v1", List.of("u1"));
    var regenerated = CourseRules.regenerate(original, "v2");
    assertEquals("v1", original.id());
    assertEquals("v2", regenerated.id());
  }

  @Test void unverifiedMenuIsAlwaysQualified() {
    assertTrue(CourseRules.safeEquipmentText("Sony A6700", "未验证的菜单路径").contains(CourseRules.UNVERIFIED_MENU_DISCLAIMER));
  }

  @Test void p0CatalogContainsAllRequiredCategories() {
    assertTrue(CourseCatalog.p0().stream().map(CourseCatalog.Course::category).collect(java.util.stream.Collectors.toSet())
        .containsAll(java.util.Set.of("BASICS", "MIRRORLESS", "EQUIPMENT", "MODEL")));
  }
}
