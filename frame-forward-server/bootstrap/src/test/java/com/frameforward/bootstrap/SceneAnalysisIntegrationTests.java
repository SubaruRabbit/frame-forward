package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class SceneAnalysisIntegrationTests {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;
    @Autowired
    JdbcTemplate jdbc;

    @Test
    void sceneAnalysisRequiresOwnedImagePersistsSnapshotsAndReturnsStructuredSafeResult() throws Exception {
        String owner = register("scene-owner");
        String other = register("scene-other");
        String ownedMedia = mediaFor(owner);
        String otherMedia = mediaFor(other);

        mvc.perform(post("/scene-analyses").header("Authorization", owner).header("Idempotency-Key", "missing-image")
                .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(request(null, Map.of()))))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/scene-analyses").header("Authorization", owner).header("Idempotency-Key", "other-image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request(otherMedia, Map.of())))).andExpect(status().isNotFound());

        Map<String, Object> fixture = Map.of("sceneType", "urban-street", "subjectCandidates", java.util.List.of("行人"),
                "light", Map.of("quality", "soft", "direction", "side"), "backgroundComplexity", "medium",
                "compositionalStructures", java.util.List.of("leading-lines"), "usablePositions",
                java.util.List.of(Map.of("description", "人行道内侧", "zone", "SIDEWALK"),
                        Map.of("description", "车道中央", "zone", "ROADWAY")),
                "accessoryOpportunities", java.util.List.of("reflector"));
        var created = mvc
                .perform(post("/scene-analyses").header("Authorization", owner).header("Idempotency-Key", "scene-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(request(ownedMedia, fixture))))
                .andExpect(status().isAccepted()).andReturn();
        String taskId = json.readTree(created.getResponse().getContentAsString()).get("taskId").asText();

        for (int i = 0; i < 40; i++) {
            var result = mvc.perform(get("/ai/tasks/{id}", taskId).header("Authorization", owner))
                    .andExpect(status().isOk()).andReturn();
            if ("SUCCEEDED".equals(json.readTree(result.getResponse().getContentAsString()).get("state").asText()))
                break;
            Thread.sleep(25);
        }
        var completed = mvc.perform(get("/ai/tasks/{id}", taskId).header("Authorization", owner))
                .andExpect(jsonPath("$.state").value("SUCCEEDED"))
                .andExpect(jsonPath("$.trace.modelId").value("qwen3.8-max"))
                .andExpect(jsonPath("$.result.light.quality").value("soft"))
                .andExpect(jsonPath("$.result.compositionalStructures[0]").value("leading-lines"))
                .andExpect(jsonPath("$.result.usablePositions.length()").value(1))
                .andExpect(jsonPath("$.result.usablePositions[0].zone").value("SIDEWALK"))
                .andExpect(jsonPath("$.result.safetyWarnings[0]").value("检测到车行道，禁止推荐在车道内取景。"))
                .andExpect(jsonPath("$.result.sceneAnalysisId").isNotEmpty()).andReturn();
        String sceneAnalysisId = json.readTree(completed.getResponse().getContentAsString())
                .at("/result/sceneAnalysisId").asText();
        mvc.perform(post("/shooting-plans").header("Authorization", owner).header("Idempotency-Key", "scene-plan-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("sceneAnalysisId", sceneAnalysisId))))
                .andExpect(status().isAccepted());
    }

    private Map<String, Object> request(String mediaId, Map<String, Object> mockOutput) {
        Map<String, Object> request = new java.util.LinkedHashMap<>();
        request.put("environmentMediaId", mediaId);
        request.put("subjectType", "PORTRAIT");
        request.put("subject", "街头人像");
        request.put("targetStyle", "电影感");
        request.put("timeConstraintMinutes", 20);
        request.put("equipmentIds", java.util.List.of());
        request.put("mockOutput", mockOutput);
        return request;
    }

    private String register(String prefix) throws Exception {
        String name = prefix.replace("-", "") + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var response = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("username", name, "email", name + "@example.com", "password", "ValidPass1!"))))
                .andExpect(status().isCreated()).andReturn();
        return "Bearer " + json.readTree(response.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private String mediaFor(String authorization) throws Exception {
        String accountId = json
                .readTree(mvc.perform(get("/test/protected").header("Authorization", authorization))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString())
                .get("accountId").asText();
        String mediaId = UUID.randomUUID().toString();
        jdbc.update(
                "INSERT INTO media (id, owner_id, content_hash, width, height, original_path, ai_copy_path) VALUES (?, ?, ?, ?, ?, ?, ?)",
                mediaId, accountId,
                UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""), 1200,
                800, "/tmp/original.jpg", "/tmp/ai.jpg");
        return mediaId;
    }
}
