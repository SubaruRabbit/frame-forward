package com.frameforward.course.model.dto;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
public record LessonContext(CourseContentVersionEntity version, String lessonId, String objective) {
    public String contentVersion() {
        return version.contentVersion;
    }
}
