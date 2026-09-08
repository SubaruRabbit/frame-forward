package com.frameforward.generation.controller;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskState;
import com.frameforward.generation.business.InvalidRequest;
import com.frameforward.generation.business.OwnershipMissing;
import com.frameforward.generation.service.ReferenceImageService;
class ReferenceImageControllerTest {
    @Test
    void acceptsJsonRequestAndPreservesTaskResponse() throws Exception {
        var service = mock(ReferenceImageService.class);
        when(service.create(eq("token"), eq("key"), any())).thenReturn(new AiTaskCreated("task", AiTaskState.QUEUED));
        var mvc = MockMvcBuilders.standaloneSetup(new ReferenceImageController(service)).build();
        mvc.perform(post("/reference-images").header("Authorization", "Bearer token").header("Idempotency-Key", "key")
                .contentType(MediaType.APPLICATION_JSON).content(
                        "{\"environmentMediaId\":\"media\",\"shootingPlanId\":\"plan\",\"selectedPlan\":{\"label\":\"SAFE\"}}"))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.taskId").value("task"))
                .andExpect(jsonPath("$.state").value("QUEUED"));
        verify(service).create(eq("token"), eq("key"), argThat(request -> "media".equals(request.environmentMediaId)
                && "plan".equals(request.shootingPlanId) && "SAFE".equals(request.selectedPlan.get("label"))));
    }
    @Test
    void preservesInvalidAndOwnershipErrorMappings() throws Exception {
        var service = mock(ReferenceImageService.class);
        when(service.create(any(), any(), any())).thenThrow(new InvalidRequest(), new OwnershipMissing());
        var mvc = MockMvcBuilders.standaloneSetup(new ReferenceImageController(service))
                .setControllerAdvice(new ReferenceImageExceptionHandler()).build();
        mvc.perform(post("/reference-images").header("Authorization", "Bearer token").header("Idempotency-Key", "key")
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REFERENCE_IMAGE"));
        mvc.perform(post("/reference-images").header("Authorization", "Bearer token").header("Idempotency-Key", "key")
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("REFERENCE_CONTEXT_NOT_FOUND"));
    }
}
