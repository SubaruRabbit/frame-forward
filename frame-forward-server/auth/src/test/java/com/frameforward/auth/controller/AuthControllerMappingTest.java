package com.frameforward.auth.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.frameforward.auth.model.dto.AccountDeletionJob;
import com.frameforward.auth.model.dto.SessionTokens;
import com.frameforward.auth.service.AuthService;

class AuthControllerMappingTest {
    private final AuthService auth = mock(AuthService.class);
    private MockMvc http;

    @BeforeEach
    void prepareControllers() {
        http = MockMvcBuilders.standaloneSetup(new AuthController(auth), new ProtectedResourceController(auth))
                .setControllerAdvice(new AuthExceptionHandler()).build();
    }

    @Test
    void sessionRequestsKeepTheirWireFieldsAndStatusCodes() throws Exception {
        var tokens = new SessionTokens("access", "refresh", 900);
        when(auth.register("user", "user@example.com", "password")).thenReturn(tokens);
        when(auth.login("user", "password")).thenReturn(tokens);
        when(auth.refresh("refresh")).thenReturn(tokens);
        http.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"user\",\"email\":\"user@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.accessToken").value("access"));
        http.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"identifier\":\"user\",\"password\":\"password\"}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
        http.perform(
                post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content("{\"refreshToken\":\"refresh\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.expiresIn").value(900));
        http.perform(post("/auth/logout").header("Authorization", "Bearer access")).andExpect(status().isNoContent());
        http.perform(
                put("/auth/password").header("Authorization", "Bearer access").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"old\",\"newPassword\":\"new\"}"))
                .andExpect(status().isNoContent());
        verify(auth).logout("access");
        verify(auth).changePassword("access", "old", "new");
    }

    @Test
    void deletionRequestsKeepDedicatedCredentialsAndAsyncStatus() throws Exception {
        var job = new AccountDeletionJob("job", "PENDING", null, "proof", null);
        when(auth.startAccountDeletion("access", "password")).thenReturn(job);
        when(auth.deletionStatus("job", "proof")).thenReturn(job);
        when(auth.retryAccountDeletion("job", "proof")).thenReturn(job);
        http.perform(post("/auth/account-deletions").header("Authorization", "Bearer access")
                .contentType(MediaType.APPLICATION_JSON).content("{\"currentPassword\":\"password\"}"))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.jobId").value("job"));
        http.perform(get("/auth/account-deletions/job").header("X-Account-Deletion-Token", "proof"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("PENDING"));
        http.perform(post("/auth/account-deletions/job").header("X-Account-Deletion-Token", "proof"))
                .andExpect(status().isAccepted());
        verify(auth).startAccountDeletion("access", "password");
        verify(auth).deletionStatus("job", "proof");
        verify(auth).retryAccountDeletion("job", "proof");
    }

    @Test
    void protectedRouteAndErrorAdviceRemainCompatible() throws Exception {
        when(auth.requireAccountId("access")).thenReturn("owner");
        http.perform(get("/test/protected").header("Authorization", "Bearer access"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.accountId").value("owner"));
        http.perform(get("/test/protected")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
        when(auth.login(anyString(), anyString())).thenThrow(new AuthService.InvalidCredentialsException());
        http.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"identifier\":\"user\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
        when(auth.register(anyString(), anyString(), anyString()))
                .thenThrow(new AuthService.ConflictException(), new AuthService.ValidationException("invalid"));
        var registration = post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"user\",\"email\":\"user@example.com\",\"password\":\"password\"}");
        http.perform(registration).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("IDENTIFIER_TAKEN"));
        http.perform(registration).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
}
