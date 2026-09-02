package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.course.CourseService;

@SpringBootTest
@AutoConfigureMockMvc
class CourseDeliveryIntegrationTests {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    CourseService courses;
    @Test
    void cachedP0CourseCreatesTraceableVersionAndDoesNotGenerateOnOpen() throws Exception {
        var name = "course" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var registration = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("username", name, "email", name + "@example.com", "password", "ValidPass1!"))))
                .andExpect(status().isCreated()).andReturn();
        var token = json.readTree(registration.getResponse().getContentAsString()).get("accessToken").asText();
        mvc.perform(get("/courses").header("Authorization", "Bearer " + token)).andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.category == 'BASICS')]").exists())
                .andExpect(jsonPath("$[?(@.category == 'MIRRORLESS')]").exists())
                .andExpect(jsonPath("$[?(@.category == 'EQUIPMENT')]").exists())
                .andExpect(jsonPath("$[?(@.category == 'MODEL')]").exists());
        mvc.perform(get("/courses/p0-basics/progress").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.contentVersion").value("p0-2026-01"));
        org.junit.jupiter.api.Assertions.assertTrue(jdbc.queryForObject(
                "SELECT COUNT(*) FROM course_content_versions WHERE course_id='p0-basics' AND model_id='qwen3.7-plus'",
                Integer.class) >= 1);
    }
    @Test
    void regenerationCreatesNewVersionAndRetainsStartedProgress() throws Exception {
        var name = "regen" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var registration = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("username", name, "email", name + "@example.com", "password", "ValidPass1!"))))
                .andExpect(status().isCreated()).andReturn();
        var token = json.readTree(registration.getResponse().getContentAsString()).get("accessToken").asText();
        mvc.perform(get("/courses/p0-basics/progress").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        var accountId = jdbc.queryForObject("SELECT id FROM accounts WHERE username=?", String.class, name);
        var oldId = jdbc.queryForObject(
                "SELECT id FROM course_content_versions WHERE course_id='p0-basics' AND content_version='p0-2026-01'",
                String.class);
        jdbc.update(
                "INSERT IGNORE INTO lesson_progress(account_id,content_version_id,lesson_id,completed_at) VALUES(?,?,?,UTC_TIMESTAMP(6))",
                accountId, oldId, "basics-exposure");
        var regenerated = courses.regenerate("p0-basics", "p0-2026-02");
        org.junit.jupiter.api.Assertions.assertNotEquals(oldId, regenerated.id());
        org.junit.jupiter.api.Assertions.assertEquals(1,
                jdbc.queryForObject("SELECT COUNT(*) FROM lesson_progress WHERE account_id=? AND content_version_id=?",
                        Integer.class, accountId, oldId));
        org.junit.jupiter.api.Assertions.assertEquals(1, jdbc.queryForObject(
                "SELECT COUNT(*) FROM course_content_versions WHERE course_id='p0-basics' AND content_version='p0-2026-02'",
                Integer.class));
    }
    @Test
    void jpegAssignmentPersistsFeedbackAndUpdatesProgress() throws Exception {
        var name = "assignment" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var registration = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("username", name, "email", name + "@example.com", "password", "ValidPass1!"))))
                .andExpect(status().isCreated()).andReturn();
        var token = json.readTree(registration.getResponse().getContentAsString()).get("accessToken").asText();
        var jpeg = jpeg();
        var upload = mvc.perform(
                multipart("/media/jpeg").file(new MockMultipartFile("file", "assignment.jpg", "image/jpeg", jpeg))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated()).andReturn();
        var mediaId = json.readTree(upload.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(post("/courses/p0-basics/lessons/basics-exposure/assignments")
                .header("Authorization", "Bearer " + token).header("Idempotency-Key", "assignment-" + name)
                .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("mediaId", mediaId))))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.lessonObjective").value("用光圈、快门和 ISO 控制曝光。"))
                .andExpect(jsonPath("$.progress.completedLessons").value(1));
    }
    private static byte[] jpeg() throws Exception {
        var image = new java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_RGB);
        var output = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(image, "jpg", output);
        return output.toByteArray();
    }
}
