package com.frameforward.ai;

import java.util.*;

/** 对课程生成结果执行确定性结构、重复和术语约束。 */
public final class CourseGenerationValidator {
  private CourseGenerationValidator() {}
  @SuppressWarnings("unchecked") public static boolean valid(Map<String,Object> output) {
    if (output==null || !(output.get("title") instanceof String title) || title.isBlank() || !(output.get("lessons") instanceof List<?> lessons) || lessons.isEmpty()) return false;
    var ids=new HashSet<String>();
    for(Object item:lessons) { if(!(item instanceof Map<?,?> raw)) return false; var lesson=(Map<String,Object>)raw; if(!(lesson.get("id") instanceof String id) || id.isBlank() || !ids.add(id) || !(lesson.get("objective") instanceof String objective) || objective.isBlank()) return false; if(lesson.containsKey("menuPath") && !(lesson.get("menuPath") instanceof String path && path.contains("官方"))) return false; }
    return true;
  }
}
