package com.frameforward.course.component;
import java.util.List;

import com.frameforward.course.model.dto.Course;
import com.frameforward.course.model.dto.Lesson;

/** P0 预生成且可离线缓存的课程目录。 */
public final class CourseCatalog {
    private CourseCatalog() {
    }
    public static List<Course> p0() {
        return List.of(
                new Course("p0-basics", "摄影基础", "BASICS", "p0-2026-01",
                        List.of(new Lesson("basics-exposure", "曝光三要素", "用光圈、快门和 ISO 控制曝光。"))),
                new Course("p0-mirrorless", "微单操作", "MIRRORLESS", "p0-2026-01",
                        List.of(new Lesson("mirrorless-focus", "自动对焦", "选择适合主体的自动对焦方式。"))),
                new Course("p0-equipment", "器材选择", "EQUIPMENT", "p0-2026-01",
                        List.of(new Lesson("equipment-lens", "镜头选择", "依据画幅和焦段选择镜头。"))),
                new Course("p0-sony-a6700", "Sony A6700 入门", "MODEL", "p0-2026-01",
                        List.of(new Lesson("a6700-focus", "A6700 对焦练习", "为静态主体建立可靠焦点。"))));
    }

}
