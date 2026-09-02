package com.frameforward.ai;

import java.util.*;

/** 对课程生成结果执行确定性结构、重复和术语约束。 */
public final class CourseGenerationValidator {
    private CourseGenerationValidator() {
    }
    @SuppressWarnings("unchecked")
    public static boolean valid(Map<String, Object> output) {
        if (!hasRequiredCourseFields(output))
            return false;
        var ids = new HashSet<String>();
        for (Object lesson : (List<?>) output.get("lessons"))
            if (!validLesson(lesson, ids))
                return false;
        return true;
    }
    private static boolean hasRequiredCourseFields(Map<String, Object> output) {
        return output != null && output.get("title") instanceof String title && !title.isBlank()
                && output.get("lessons") instanceof List<?> lessons && !lessons.isEmpty();
    }
    private static boolean validLesson(Object item, Set<String> ids) {
        if (!(item instanceof Map<?, ?> raw))
            return false;
        var lesson = (Map<String, Object>) raw;
        return hasUniqueLessonIdentity(lesson, ids) && hasValidMenuPath(lesson);
    }
    private static boolean hasUniqueLessonIdentity(Map<String, Object> lesson, Set<String> ids) {
        return lesson.get("id") instanceof String id && !id.isBlank() && ids.add(id)
                && lesson.get("objective") instanceof String objective && !objective.isBlank();
    }
    private static boolean hasValidMenuPath(Map<String, Object> lesson) {
        return !lesson.containsKey("menuPath") || lesson.get("menuPath") instanceof String path && path.contains("官方");
    }
}
