package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class AccountDeletionIntegrationTests {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;
    @Autowired
    JdbcTemplate jdbc;

    @Test
    void wrongPasswordDoesNotChangeData() throws Exception {
        Session session = register("wrong" + suffix());

        mvc.perform(post("/auth/account-deletions").header("Authorization", "Bearer " + session.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new DeleteRequest("WrongPass1!"))))
                .andExpect(status().isUnauthorized());

        org.junit.jupiter.api.Assertions.assertEquals(1, jdbc
                .queryForObject("SELECT COUNT(*) FROM accounts WHERE username = ?", Integer.class, session.username()));
        mvc.perform(get("/test/protected").header("Authorization", "Bearer " + session.accessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void deletionRevokesSessionsAndItsCredentialOnlyReadsTheDeletionJob() throws Exception {
        Session session = register("delete" + suffix());
        var start = mvc
                .perform(post("/auth/account-deletions").header("Authorization", "Bearer " + session.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new DeleteRequest("ValidPass1!"))))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.jobId").exists())
                .andExpect(jsonPath("$.deletionToken").exists()).andReturn();
        JsonNode job = json.readTree(start.getResponse().getContentAsString());

        mvc.perform(get("/test/protected").header("Authorization", "Bearer " + session.accessToken()))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Login(session.username(), "ValidPass1!"))))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/auth/account-deletions/" + job.get("jobId").asText()).header("X-Account-Deletion-Token",
                job.get("deletionToken").asText())).andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(job.get("jobId").asText()));
    }

    private Session register(String username) throws Exception {
        var result = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(
                        json.writeValueAsString(new Register(username, username + "@example.com", "ValidPass1!"))))
                .andExpect(status().isCreated()).andReturn();
        JsonNode tokens = json.readTree(result.getResponse().getContentAsString());
        return new Session(username, tokens.get("accessToken").asText());
    }

    private static String suffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
    record Register(String username, String email, String password) {
    }
    record Login(String identifier, String password) {
    }
    record DeleteRequest(String currentPassword) {
    }
    record Session(String username, String accessToken) {
    }
}
