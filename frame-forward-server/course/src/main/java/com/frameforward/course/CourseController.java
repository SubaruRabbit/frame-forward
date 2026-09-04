package com.frameforward.course;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.frameforward.auth.AuthController;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courses;
    public CourseController(CourseService courses) {
        this.courses = courses;
    }
    @GetMapping
    public List<CourseCatalog.Course> catalog(
            @RequestHeader(name = "Authorization", required = false) String authorization) {
        return courses.catalog(AuthController.bearer(authorization));
    }
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseCatalog.Course> course(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String courseId) {
        try {
            return ResponseEntity.ok(courses.course(AuthController.bearer(authorization), courseId));
        } catch (CourseService.NotFound exception) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{courseId}/progress")
    public CourseService.Progress progress(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String courseId) {
        return courses.progress(AuthController.bearer(authorization), courseId);
    }
    @PostMapping("/{courseId}/lessons/{lessonId}/assignments")
    public ResponseEntity<CourseService.Feedback> submit(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @PathVariable String courseId, @PathVariable String lessonId,
            @RequestBody Assignment request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courses.submit(AuthController.bearer(authorization), courseId, lessonId, request.mediaId, key));
    }
    public record Assignment(String mediaId) {
    }
}
