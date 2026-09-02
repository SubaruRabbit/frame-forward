package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class RetakeComparisonIntegrationTests {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;
    @Autowired
    JdbcTemplate jdbc;

    @Test
    void linksOwnedCompletedEvaluationsFromOneSessionAndCalculatesStableDeltas() throws Exception {
        String token = register();
        String session = session(token, "主体居中、降低背景干扰");
        String original = evaluate(token, media(token, Map.of("fNumber", "f/1.8", "iso", 800, "focalLength", "50mm")),
                session, output(60, Map.of("composition", 50, "light", 70), List.of("背景杂乱")));
        String retake = evaluate(token, media(token, Map.of("fNumber", "f/2.8", "iso", 200, "focalLength", "50mm")),
                session, output(78, Map.of("composition", 75, "light", 72), List.of()));

        mvc.perform(post("/retake-comparisons").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(json
                        .writeValueAsString(Map.of("originalEvaluationId", original, "retakeEvaluationId", retake))))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.original.mediaId").exists())
                .andExpect(jsonPath("$.retake.mediaId").exists()).andExpect(jsonPath("$.scoreDelta").value(18))
                .andExpect(jsonPath("$.dimensionChanges.composition").value(25))
                .andExpect(jsonPath("$.parameterChanges.iso.before").value("800"))
                .andExpect(jsonPath("$.parameterChanges.iso.after").value("200"))
                .andExpect(jsonPath("$.compositionChanges[0]").value("主体居中、降低背景干扰"))
                .andExpect(jsonPath("$.improvedProblems[0]").value("背景杂乱"))
                .andExpect(jsonPath("$.remainingProblems").isEmpty())
                .andExpect(jsonPath("$.nextPracticeAdvice").isNotEmpty());
    }

    @Test
    void rejectsCrossSessionAndCrossUserRetakeLinks() throws Exception {
        String first = register();
        String second = register();
        String sessionA = session(first, "保持机位");
        String sessionB = session(first, "调整视角");
        String original = evaluate(first, media(first, Map.of()), sessionA,
                output(60, Map.of("composition", 60), List.of("背景杂乱")));
        String otherSession = evaluate(first, media(first, Map.of()), sessionB,
                output(70, Map.of("composition", 70), List.of()));

        mvc.perform(post("/retake-comparisons").header("Authorization", first).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(
                        Map.of("originalEvaluationId", original, "retakeEvaluationId", otherSession))))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/retake-comparisons").header("Authorization", second).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(
                        Map.of("originalEvaluationId", original, "retakeEvaluationId", otherSession))))
                .andExpect(status().isNotFound());
    }

    private String session(String token, String planContext) throws Exception {
        String account = account(token);
        String plan = UUID.randomUUID().toString();
        jdbc.update(
                "INSERT INTO shooting_plans (id,account_id,scene_analysis_id,ai_task_id,scene_snapshot_json,equipment_snapshot_json,created_at) VALUES (?,?,?,?,JSON_OBJECT(),JSON_OBJECT(),CURRENT_TIMESTAMP(6))",
                plan, account, UUID.randomUUID().toString(), UUID.randomUUID().toString());
        var response = mvc
                .perform(post("/shooting-sessions").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("shootingPlanId", plan, "planContext", planContext))))
                .andExpect(status().isCreated()).andReturn();
        return json.readTree(response.getResponse().getContentAsString()).get("id").asText();
    }

    private String evaluate(String token, String media, String session, Map<String, Object> output) throws Exception {
        var response = mvc
                .perform(post("/photo-evaluations").header("Authorization", token)
                        .header("Idempotency-Key", UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("mediaId", media, "sessionId", session, "mockOutput", output))))
                .andExpect(status().isAccepted()).andReturn();
        String task = json.readTree(response.getResponse().getContentAsString()).get("taskId").asText();
        for (int i = 0; i < 40; i++) {
            JsonNode state = json.readTree(mvc.perform(get("/ai/tasks/{id}", task).header("Authorization", token))
                    .andReturn().getResponse().getContentAsString());
            if ("SUCCEEDED".equals(state.get("state").asText()))
                break;
            Thread.sleep(25);
        }
        mvc.perform(get("/photo-evaluations/{id}", task).header("Authorization", token)).andExpect(status().isOk());
        return jdbc.queryForObject("SELECT id FROM photo_evaluations WHERE ai_task_id = ?", String.class, task);
    }

    private Map<String, Object> output(int total, Map<String, Integer> dimensions, List<String> problems) {
        return Map.of("total", total, "dimensions", dimensions, "strengths", List.of("构图清晰"), "primaryProblems",
                problems, "priorityImprovement", "整理背景", "technicalDiagnosis",
                Map.of("certainty", "OBSERVATION", "text", "稳定"), "retakeSteps", List.of("固定机位"));
    }

    private String media(String token, Map<String, Object> exif) throws Exception {
        String id = UUID.randomUUID().toString();
        jdbc.update(
                "INSERT INTO media (id,owner_id,content_hash,width,height,original_path,ai_copy_path,exif_json) VALUES (?,?,?,?,?,?,?,CAST(? AS JSON))",
                id, account(token),
                UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""), 1200,
                800, "/tmp/o.jpg", "/tmp/a.jpg", json.writeValueAsString(exif));
        return id;
    }
    private String account(String token) throws Exception {
        return json.readTree(mvc.perform(get("/test/protected").header("Authorization", token)).andReturn()
                .getResponse().getContentAsString()).get("accountId").asText();
    }
    private String register() throws Exception {
        String name = "retake" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var response = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("username", name, "email", name + "@example.com", "password", "ValidPass1!"))))
                .andReturn();
        return "Bearer " + json.readTree(response.getResponse().getContentAsString()).get("accessToken").asText();
    }
}
