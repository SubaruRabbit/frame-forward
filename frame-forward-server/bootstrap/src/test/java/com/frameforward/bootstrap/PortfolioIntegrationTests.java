package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class PortfolioIntegrationTests {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;
    @Autowired
    JdbcTemplate jdbc;

    @Test
    void listsOnlyOwnedWorksFiltersExifAndKeepsPartialDetailsAvailable() throws Exception {
        String owner = register();
        String other = register();
        String matched = media(owner, Map.of("camera", "Nikon Zf", "lens", "NIKKOR Z 40mm"));
        media(owner, Map.of("camera", "Sony A7", "lens", "FE 50mm"));
        media(other, Map.of("camera", "Nikon Zf", "lens", "NIKKOR Z 40mm"));

        mvc.perform(get("/portfolio/works").header("Authorization", owner).param("camera", "Nikon Zf").param("lens",
                "40mm")).andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].mediaId").value(matched));

        mvc.perform(get("/portfolio/works/{mediaId}", matched).header("Authorization", owner))
                .andExpect(status().isOk()).andExpect(jsonPath("$.availability.exif").value(true))
                .andExpect(jsonPath("$.availability.evaluation").value(false))
                .andExpect(jsonPath("$.availability.sourcePlan").value(false))
                .andExpect(jsonPath("$.availability.retake").value(false));

        mvc.perform(get("/portfolio/works/{mediaId}", matched).header("Authorization", other))
                .andExpect(status().isNotFound());
    }

    @Test
    void favoriteToggleChangesOnlyTheRequestedOwnedWork() throws Exception {
        String token = register();
        String first = media(token, Map.of());
        String second = media(token, Map.of());

        mvc.perform(put("/portfolio/works/{mediaId}", first).header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("favorite", true))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.favorite").value(true));
        mvc.perform(get("/portfolio/works/{mediaId}", first).header("Authorization", token))
                .andExpect(jsonPath("$.favorite").value(true));
        mvc.perform(get("/portfolio/works/{mediaId}", second).header("Authorization", token))
                .andExpect(jsonPath("$.favorite").value(false));
    }

    @Test
    void deletionIsIdempotentHidesTheWorkAndKeepsUnrelatedWork() throws Exception {
        String token = register();
        String target = media(token, Map.of("camera", "Nikon Zf"));
        String remaining = media(token, Map.of("camera", "Sony A7"));
        String account = account(token);
        jdbc.update(
                "INSERT INTO photo_evaluations (id,account_id,media_id,content_hash,rule_version,execution_version,ai_task_id,created_at) VALUES (?,?,?,?,?,?,?,CURRENT_TIMESTAMP)",
                UUID.randomUUID().toString(), account, target,
                UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""), "v1",
                "v1", UUID.randomUUID().toString());
        jdbc.update(
                "INSERT INTO photo_evaluations (id,account_id,media_id,content_hash,rule_version,execution_version,ai_task_id,created_at) VALUES (?,?,?,?,?,?,?,CURRENT_TIMESTAMP)",
                UUID.randomUUID().toString(), account, remaining,
                UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""), "v1",
                "v1", UUID.randomUUID().toString());

        String jobId = json
                .readTree(mvc.perform(delete("/portfolio/works/{mediaId}", target).header("Authorization", token))
                        .andExpect(status().isAccepted()).andExpect(jsonPath("$.mediaId").value(target)).andReturn()
                        .getResponse().getContentAsString())
                .get("jobId").asText();
        mvc.perform(delete("/portfolio/works/{mediaId}", target).header("Authorization", token))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.jobId").value(jobId));
        mvc.perform(get("/portfolio/works/{mediaId}", target).header("Authorization", token))
                .andExpect(status().isNotFound());
        mvc.perform(get("/portfolio/works/{mediaId}", remaining).header("Authorization", token))
                .andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertEquals(0,
                jdbc.queryForObject("SELECT COUNT(*) FROM media WHERE id = ?", Integer.class, target));
        org.junit.jupiter.api.Assertions.assertEquals(0, jdbc
                .queryForObject("SELECT COUNT(*) FROM photo_evaluations WHERE media_id = ?", Integer.class, target));
        org.junit.jupiter.api.Assertions.assertEquals(1, jdbc
                .queryForObject("SELECT COUNT(*) FROM photo_evaluations WHERE media_id = ?", Integer.class, remaining));
        org.junit.jupiter.api.Assertions.assertEquals(1,
                jdbc.queryForObject("SELECT COUNT(*) FROM media WHERE id = ?", Integer.class, remaining));
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
        String name = "portfolio" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var response = mvc
                .perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("username", name, "email", name + "@example.com", "password", "ValidPass1!"))))
                .andReturn();
        return "Bearer " + json.readTree(response.getResponse().getContentAsString()).get("accessToken").asText();
    }
}
