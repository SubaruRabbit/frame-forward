package com.frameforward.course.controller;
import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.frameforward.auth.service.AuthService;
import com.frameforward.course.business.CourseNotFound;
import com.frameforward.course.model.dto.Assignment;
import com.frameforward.course.model.dto.Course;
import com.frameforward.course.model.dto.Feedback;
import com.frameforward.course.model.dto.Progress;
import com.frameforward.course.service.CourseService;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courses;
    public CourseController(CourseService courses) {
        this.courses = courses;
    }
    @GetMapping
    public List<Course> catalog(@RequestHeader(name = "Authorization", required = false) String authorization) {
        return courses.catalog(AuthService.bearer(authorization));
    }
    @GetMapping("/{courseId}")
    public ResponseEntity<Course> course(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String courseId) {
        try {
            return ResponseEntity.ok(courses.course(AuthService.bearer(authorization), courseId));
        } catch (CourseNotFound exception) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{courseId}/progress")
    public Progress progress(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String courseId) {
        return courses.progress(AuthService.bearer(authorization), courseId);
    }
    @PostMapping("/{courseId}/lessons/{lessonId}/assignments")
    public ResponseEntity<Feedback> submit(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @PathVariable String courseId, @PathVariable String lessonId,
            @RequestBody Assignment request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courses.submit(AuthService.bearer(authorization), courseId, lessonId, request.mediaId(), key));
    }

}
