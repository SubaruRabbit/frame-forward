package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
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
class ReferenceImageIntegrationTests {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;
    @Autowired
    JdbcTemplate jdbc;
    @Test
    void rejectsForeignContextAndStoresAOwnedGeneratedMediaResult() throws Exception {
        String owner = register("reference-owner"), other = register("reference-other");
        String ownedMedia = media(owner), ownedPlan = plan(owner), foreignMedia = media(other),
                foreignPlan = plan(other);
        Map<String, Object> request = Map.of("environmentMediaId", ownedMedia, "shootingPlanId", ownedPlan,
                "selectedPlan", selectedPlan(), "mockOutput",
                Map.of("imageUrl", "generated/reference.jpg", "width", 1536, "height", 1024));
        mvc.perform(post("/reference-images").header("Authorization", owner).header("Idempotency-Key", "foreign-media")
                .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("environmentMediaId",
                        foreignMedia, "shootingPlanId", ownedPlan, "selectedPlan", selectedPlan()))))
                .andExpect(status().isNotFound());
        mvc.perform(post("/reference-images").header("Authorization", owner).header("Idempotency-Key", "foreign-plan")
                .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("environmentMediaId",
                        ownedMedia, "shootingPlanId", foreignPlan, "selectedPlan", selectedPlan()))))
                .andExpect(status().isNotFound());
        var created = mvc.perform(
                post("/reference-images").header("Authorization", owner).header("Idempotency-Key", "owned-reference")
                        .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(request)))
                .andExpect(status().isAccepted()).andReturn();
        String taskId = json.readTree(created.getResponse().getContentAsString()).get("taskId").asText();
        for (int i = 0; i < 40; i++) {
            var result = mvc.perform(get("/ai/tasks/{id}", taskId).header("Authorization", owner)).andReturn();
            String state = json.readTree(result.getResponse().getContentAsString()).get("state").asText();
            if ("SUCCEEDED".equals(state) || "FAILED".equals(state))
                break;
            Thread.sleep(25);
        }
        mvc.perform(get("/ai/tasks/{id}", taskId).header("Authorization", owner))
                .andExpect(jsonPath("$.state").value("SUCCEEDED"))
                .andExpect(jsonPath("$.trace.modelId").value("qwen-image-3.0-pro"))
                .andExpect(jsonPath("$.result.mediaId").isNotEmpty());
    }
    private Map<String, Object> selectedPlan() {
        return Map.of("label", "SAFE", "position", "人行道内侧", "cameraHeight", "胸口高度", "composition", "引导线", "orientation",
                "竖构图", "focalLengthMm", 35, "exposure",
                Map.of("aperture", "f/2.8", "shutterSpeed", "1/250s", "iso", 400, "startingPoint", true));
    }
    private String media(String token) throws Exception {
        String account = account(token), id = UUID.randomUUID().toString();
        jdbc.update(
                "INSERT INTO media (id, owner_id, content_hash, width, height, original_path, ai_copy_path, exif_json) VALUES (?, ?, ?, ?, ?, ?, ?, JSON_OBJECT())",
                id, account,
                UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""), 100, 100,
                "source.jpg", "source.jpg");
        return id;
    }
    private String plan(String token) throws Exception {
        String account = account(token), id = UUID.randomUUID().toString();
        jdbc.update(
                "INSERT INTO shooting_plans (id, account_id, scene_analysis_id, ai_task_id, scene_snapshot_json, equipment_snapshot_json, created_at) VALUES (?, ?, ?, ?, JSON_OBJECT(), JSON_ARRAY(), ?)",
                id, account, UUID.randomUUID().toString(), UUID.randomUUID().toString(), Instant.now());
        return id;
    }
    private String account(String token) throws Exception {
        return json.readTree(mvc.perform(get("/test/protected").header("Authorization", token)).andReturn()
                .getResponse().getContentAsString()).get("accountId").asText();
    }
    private String register(String prefix) throws Exception {
        String name = prefix.replace("-", "_") + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var response = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("username", name, "email", name + "@example.com", "password", "ValidPass1!"))))
                .andExpect(status().isCreated()).andReturn();
        return "Bearer " + json.readTree(response.getResponse().getContentAsString()).get("accessToken").asText();
    }
}
