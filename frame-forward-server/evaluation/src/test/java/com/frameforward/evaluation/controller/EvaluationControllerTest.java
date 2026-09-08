package com.frameforward.evaluation.controller;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskState;
import com.frameforward.ai.model.dto.AiTaskStatus;
import com.frameforward.evaluation.business.*;
import com.frameforward.evaluation.model.entity.ShootingSessionEntity;
import com.frameforward.evaluation.service.*;
class EvaluationControllerTest {
    @Test
    void preservesEvaluationSessionAndRetakeRoutes() throws Exception {
        var photos = mock(PhotoEvaluationService.class);
        var sessions = mock(ShootingSessionService.class);
        var retakes = mock(RetakeComparisonService.class);
        var session = new ShootingSessionEntity();
        session.id = "session";
        when(photos.create(eq("token"), eq("key"), any())).thenReturn(new AiTaskCreated("task", AiTaskState.QUEUED));
        when(photos.result("token", "task")).thenReturn(new AiTaskStatus("task", AiTaskState.QUEUED, null, null, null));
        when(sessions.create(eq("token"), any())).thenReturn(session);
        when(retakes.create(eq("token"), any())).thenReturn(Map.of("scoreDelta", 5));
        when(retakes.get("token", "retake")).thenReturn(Map.of("scoreDelta", 5));
        var mvc = MockMvcBuilders
                .standaloneSetup(new PhotoEvaluationController(photos), new ShootingSessionController(sessions),
                        new RetakeComparisonController(retakes))
                .setControllerAdvice(new PhotoEvaluationExceptionHandler()).build();
        mvc.perform(post("/photo-evaluations").header("Authorization", "Bearer token").header("Idempotency-Key", "key")
                .contentType(MediaType.APPLICATION_JSON).content("{\"mediaId\":\"media\"}"))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.taskId").value("task"));
        verify(photos).create(eq("token"), eq("key"), argThat(request -> "media".equals(request.mediaId)));
        mvc.perform(get("/photo-evaluations/task").header("Authorization", "Bearer token")).andExpect(status().isOk());
        mvc.perform(post("/shooting-sessions").header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("session"));
        mvc.perform(post("/retake-comparisons").header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.scoreDelta").value(5));
        mvc.perform(get("/retake-comparisons/retake").header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
        when(photos.create(any(), any(), any())).thenThrow(new PhotoEvaluationInvalid(), new PhotoEvaluationNotFound());
        mvc.perform(post("/photo-evaluations").header("Authorization", "Bearer token").header("Idempotency-Key", "key")
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_EVALUATION"));
        mvc.perform(post("/photo-evaluations").header("Authorization", "Bearer token").header("Idempotency-Key", "key")
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EVALUATION_MEDIA_NOT_FOUND"));
    }
}
