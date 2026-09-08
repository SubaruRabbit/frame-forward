package com.frameforward.portfolio.controller;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.frameforward.portfolio.business.PortfolioNotFound;
import com.frameforward.portfolio.model.dto.*;
import com.frameforward.portfolio.service.PortfolioService;
class PortfolioControllerTest {
    @Test
    void preservesRoutesJsonAndDeletionStatus() throws Exception {
        var service = mock(PortfolioService.class);
        var job = new DeletionJob("job", "media", "COMPLETED", null);
        when(service.list(eq("token"), any())).thenReturn(new Page(List.of(Map.of("mediaId", "media")), null));
        when(service.detail("token", "media")).thenReturn(Map.of("mediaId", "media"));
        when(service.setFavorite("token", "media", true)).thenReturn(new Favorite("media", true));
        when(service.delete("token", "media")).thenReturn(job);
        when(service.deletionStatus("token", "job")).thenReturn(job);
        var mvc = MockMvcBuilders
                .standaloneSetup(new PortfolioController(service), new PortfolioWorkDeletionController(service))
                .setControllerAdvice(new PortfolioExceptionHandler()).build();
        mvc.perform(get("/portfolio/works").header("Authorization", "Bearer token").param("limit", "2"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].mediaId").value("media"));
        verify(service).list(eq("token"), argThat(filter -> filter.limit() == 2));
        mvc.perform(get("/portfolio/works/media").header("Authorization", "Bearer token")).andExpect(status().isOk())
                .andExpect(jsonPath("$.mediaId").value("media"));
        mvc.perform(put("/portfolio/works/media").header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON).content("{\"favorite\":true}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(true));
        mvc.perform(delete("/portfolio/works/media").header("Authorization", "Bearer token"))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.jobId").value("job"));
        mvc.perform(get("/portfolio/work-deletions/job").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("COMPLETED"));
        when(service.detail("token", "missing")).thenThrow(new PortfolioNotFound());
        mvc.perform(get("/portfolio/works/missing").header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }
}
