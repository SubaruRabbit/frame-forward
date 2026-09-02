package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
class AuthenticationIntegrationTests {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;
    @Autowired
    JdbcTemplate jdbc;
    @Test
    void registrationPasswordSessionAndProtectionFlow() throws Exception {
        var suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var username = "user" + suffix;
        var email = username + "@example.com";
        var registration = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Register(username, email, "ValidPass1!"))))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.accessToken").exists()).andReturn();
        var tokens = json.readTree(registration.getResponse().getContentAsString());
        var access = tokens.get("accessToken").asText();
        var refresh = tokens.get("refreshToken").asText();
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Register(username.toUpperCase(), "other" + email, "ValidPass1!"))))
                .andExpect(status().isConflict());
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Register("other" + username, email.toUpperCase(), "ValidPass1!"))))
                .andExpect(status().isConflict());
        mvc.perform(get("/test/protected")).andExpect(status().isUnauthorized());
        mvc.perform(get("/test/protected").header("Authorization", "Bearer " + access)).andExpect(status().isOk());
        var rotated = mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Refresh(refresh)))).andExpect(status().isOk()).andReturn();
        mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Refresh(refresh)))).andExpect(status().isUnauthorized());
        var newAccess = json.readTree(rotated.getResponse().getContentAsString()).get("accessToken").asText();
        mvc.perform(put("/auth/password").header("Authorization", "Bearer " + newAccess)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Change("ValidPass1!", "ChangedPass2@"))))
                .andExpect(status().isNoContent());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Login(username, "ValidPass1!"))))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Login(username, "ChangedPass2@")))).andExpect(status().isOk());
    }
    @Test
    void expiredRefreshSessionIsRejected() throws Exception {
        var suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var username = "expired" + suffix;
        var result = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(
                        json.writeValueAsString(new Register(username, username + "@example.com", "ValidPass1!"))))
                .andExpect(status().isCreated()).andReturn();
        var refresh = json.readTree(result.getResponse().getContentAsString()).get("refreshToken").asText();
        jdbc.update(
                "UPDATE refresh_sessions SET expires_at = DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 1 SECOND) WHERE account_id = (SELECT id FROM accounts WHERE username = ?)",
                username);
        mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Refresh(refresh)))).andExpect(status().isUnauthorized());
    }
    record Register(String username, String email, String password) {
    }
    record Refresh(String refreshToken) {
    }
    record Login(String identifier, String password) {
    }
    record Change(String currentPassword, String newPassword) {
    }
}
