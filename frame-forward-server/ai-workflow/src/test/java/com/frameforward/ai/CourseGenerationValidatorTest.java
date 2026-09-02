package com.frameforward.ai;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
class CourseGenerationValidatorTest {
    @Test
    void rejectsMissingSchemaDuplicateLessonsAndInventedMenus() {
        assertFalse(CourseGenerationValidator.valid(Map.of()));
        assertFalse(CourseGenerationValidator.valid(Map.of("title", "x", "lessons",
                java.util.List.of(Map.of("id", "a", "objective", "x"), Map.of("id", "a", "objective", "x")))));
        assertFalse(CourseGenerationValidator.valid(Map.of("title", "x", "lessons",
                java.util.List.of(Map.of("id", "a", "objective", "x", "menuPath", "未验证")))));
    }
    @Test
    void acceptsValidCourseAndRejectsIncorrectLessonTypes() {
        assertTrue(CourseGenerationValidator.valid(Map.of("title", "构图入门", "lessons",
                List.of(Map.of("id", "a", "objective", "理解三分法", "menuPath", "官方/课程")))));
        assertFalse(CourseGenerationValidator.valid(Map.of("title", "x", "lessons", List.of("not-a-lesson"))));
        assertFalse(CourseGenerationValidator
                .valid(Map.of("title", "x", "lessons", List.of(Map.of("id", 1, "objective", "目标")))));
    }
}
