package com.frameforward.media.controller;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.frameforward.auth.service.AuthService;
import com.frameforward.media.model.dto.MediaResponse;
import com.frameforward.media.model.dto.UploadProgress;
import com.frameforward.media.service.MediaService;

class MediaControllerMappingTest {
    @Test
    void mapsUploadReadAndProgressWithoutChangingAccountOrPayload() throws Exception {
        var auth = mock(AuthService.class);
        var service = mock(MediaService.class);
        var mvc = MockMvcBuilders.standaloneSetup(new MediaController(service, auth)).build();
        when(auth.requireAccountId("token")).thenReturn("owner");
        var response = new MediaResponse("id", 20, 10, "hash", "COMPLETED", "{}");
        when(service.ingest(eq("owner"), any())).thenReturn(response);
        when(service.get("owner", "id")).thenReturn(response);
        when(service.progress("owner", "id")).thenReturn(new UploadProgress("id", "COMPLETED", 100));
        var upload = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1});
        mvc.perform(multipart("/media/jpeg").file(upload).header("Authorization", "Bearer token"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.contentHash").value("hash"));
        mvc.perform(get("/media/id").header("Authorization", "Bearer token")).andExpect(status().isOk())
                .andExpect(jsonPath("$.width").value(20));
        mvc.perform(get("/media/uploads/id").header("Authorization", "Bearer token")).andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(100));
        verify(service).ingest("owner", upload);
        verify(service).get("owner", "id");
        verify(service).progress("owner", "id");
    }

    @Test
    void mapsInvalidOversizedAndMissingMediaErrors() throws Exception {
        var service = mock(MediaService.class);
        var auth = mock(AuthService.class);
        var mvc = MockMvcBuilders.standaloneSetup(new MediaController(service, auth))
                .setControllerAdvice(new MediaExceptionHandler()).build();
        when(auth.requireAccountId("token")).thenReturn("owner");
        var invalid = mock(MediaService.InvalidMediaException.class);
        when(invalid.getMessage()).thenReturn("invalid JPEG");
        when(service.get("owner", "id")).thenThrow(invalid, new MediaService.TooLargeException(),
                new MediaService.NotFoundException());
        mvc.perform(get("/media/id").header("Authorization", "Bearer token")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("invalid JPEG"));
        mvc.perform(get("/media/id").header("Authorization", "Bearer token")).andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.code").value("PAYLOAD_TOO_LARGE"));
        mvc.perform(get("/media/id").header("Authorization", "Bearer token")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
