package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ShootingPlanIntegrationTests {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;
    @Autowired
    JdbcTemplate jdbc;
    @Test
    void generatesTwoRankedSafePlansForOwnedScene() throws Exception {
        String owner = register("planowner");
        String other = register("planother");
        String ownerScene = scene(owner);
        String otherScene = scene(other);
        mvc.perform(post("/shooting-plans").header("Authorization", owner).header("Idempotency-Key", "other-scene")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("sceneAnalysisId", otherScene, "mockOutput", plans()))))
                .andExpect(status().isNotFound());
        var created = mvc
                .perform(post("/shooting-plans").header("Authorization", owner).header("Idempotency-Key", "owned-scene")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("sceneAnalysisId", ownerScene, "mockOutput", plans()))))
                .andExpect(status().isAccepted()).andReturn();
        String id = json.readTree(created.getResponse().getContentAsString()).get("taskId").asText();
        for (int i = 0; i < 40; i++) {
            var response = mvc.perform(get("/ai/tasks/{id}", id).header("Authorization", owner)).andReturn();
            if ("SUCCEEDED".equals(json.readTree(response.getResponse().getContentAsString()).get("state").asText()))
                break;
            Thread.sleep(25);
        }
        mvc.perform(get("/ai/tasks/{id}", id).header("Authorization", owner))
                .andExpect(jsonPath("$.state").value("SUCCEEDED"))
                .andExpect(jsonPath("$.trace.modelId").value("qwen3.7-plus"))
                .andExpect(jsonPath("$.result.plans.length()").value(2))
                .andExpect(jsonPath("$.result.plans[0].recommended").value(true))
                .andExpect(jsonPath("$.result.plans[0].exposure.startingPoint").value(true));
    }
    private Map<String, Object> plans() {
        return Map.of("plans", List.of(plan("SAFE", true), plan("ATMOSPHERIC", false)));
    }
    private Map<String, Object> plan(String label, boolean recommended) {
        return Map.ofEntries(Map.entry("label", label), Map.entry("recommended", recommended),
                Map.entry("position", "人行道内侧"), Map.entry("distance", "2 米"), Map.entry("cameraHeight", "胸口高度"),
                Map.entry("orientation", "竖构图"), Map.entry("focalLengthMm", 35),
                Map.entry("exposure",
                        Map.of("aperture", "f/2.8", "shutterSpeed", "1/250s", "iso", 400, "startingPoint", true)),
                Map.entry("metering", "评价测光"), Map.entry("focus", "单次自动对焦"), Map.entry("driveMode", "单张"),
                Map.entry("whiteBalance", "自动"), Map.entry("composition", "引导线"), Map.entry("pose", "自然站立"),
                Map.entry("accessoryUse", "无需附件"), Map.entry("steps", List.of("确认人行道安全", "完成试拍")));
    }
    private String scene(String authorization) throws Exception {
        String account = json.readTree(mvc.perform(get("/test/protected").header("Authorization", authorization))
                .andReturn().getResponse().getContentAsString()).get("accountId").asText();
        String id = UUID.randomUUID().toString();
        jdbc.update(
                "INSERT INTO scene_analyses (id, account_id, environment_media_id, subject_type, subject_text, target_style, time_constraint_minutes, equipment_snapshot_json, ai_task_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, JSON_ARRAY(), ?, ?)",
                id, account, UUID.randomUUID().toString(), "PORTRAIT", "人像", "自然", 30, UUID.randomUUID().toString(),
                Instant.now());
        return id;
    }
    private String register(String prefix) throws Exception {
        String name = prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var response = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("username", name, "email", name + "@example.com", "password", "ValidPass1!"))))
                .andExpect(status().isCreated()).andReturn();
        return "Bearer " + json.readTree(response.getResponse().getContentAsString()).get("accessToken").asText();
    }
}
