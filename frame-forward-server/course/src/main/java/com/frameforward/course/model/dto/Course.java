package com.frameforward.course.model.dto;
import java.util.List;
public record Course(String id, String title, String category, String contentVersion, List<Lesson> lessons) {
    public Course {
        lessons = List.copyOf(lessons);
    }
}
