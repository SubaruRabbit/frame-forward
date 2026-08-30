package com.frameforward.ai;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.Test;
class CourseGenerationValidatorTest {
 @Test void rejectsMissingSchemaDuplicateLessonsAndInventedMenus() {
   assertFalse(CourseGenerationValidator.valid(Map.of()));
   assertFalse(CourseGenerationValidator.valid(Map.of("title","x","lessons",java.util.List.of(Map.of("id","a","objective","x"),Map.of("id","a","objective","x")))));
   assertFalse(CourseGenerationValidator.valid(Map.of("title","x","lessons",java.util.List.of(Map.of("id","a","objective","x","menuPath","未验证")))));
 }
}
