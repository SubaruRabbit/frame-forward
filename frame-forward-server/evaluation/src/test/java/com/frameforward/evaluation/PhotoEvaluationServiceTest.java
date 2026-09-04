package com.frameforward.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.media.MediaEntity;
import com.frameforward.media.MediaManager;

class PhotoEvaluationServiceTest {
    private final AuthService auth = mock(AuthService.class);
    private final MediaManager media = mock(MediaManager.class);
    private final AiTaskRuntime tasks = mock(AiTaskRuntime.class);
    private final EvaluationManager manager = mock(EvaluationManager.class);
    private final PhotoEvaluationService service = new PhotoEvaluationService(auth, media, tasks, new ObjectMapper(),
            new EvaluationBusiness(manager));

    @Test
    void createsEvaluationAndPersistsItsTaskForNewPhoto() {
        MediaEntity photo = new MediaEntity();
        photo.id = "media-1";
        photo.contentHash = "hash-1";
        photo.exifJson = "{}";
        AiTaskRuntime.Created created = new AiTaskRuntime.Created("task-1", AiTaskRuntime.State.QUEUED);
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.findOwned("account-1", "media-1")).thenReturn(photo);
        when(manager.findCached(any(), any(), any(), anyBoolean())).thenReturn(null);
        when(tasks.create(any(), any(), any())).thenReturn(created);

        assertEquals(created, service.create("token", "key", request("media-1")));

        ArgumentCaptor<PhotoEvaluationEntity> entity = ArgumentCaptor.forClass(PhotoEvaluationEntity.class);
        verify(manager).save(entity.capture());
        assertEquals("account-1", entity.getValue().accountId);
        assertEquals("media-1", entity.getValue().mediaId);
        assertEquals("hash-1", entity.getValue().contentHash);
        assertEquals("task-1", entity.getValue().aiTaskId);
    }

    @Test
    void rejectsRequestForMissingPhoto() {
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.findOwned("account-1", "missing")).thenReturn(null);

        assertThrows(PhotoEvaluationService.NotFound.class, () -> service.create("token", "key", request("missing")));

        verifyNoInteractions(manager, tasks);
    }

    @Test
    void reusesCachedTaskWhenReanalysisIsNotRequested() {
        MediaEntity photo = new MediaEntity();
        photo.contentHash = "hash-1";
        PhotoEvaluationEntity cached = new PhotoEvaluationEntity();
        cached.aiTaskId = "cached-task";
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.findOwned("account-1", "media-1")).thenReturn(photo);
        when(manager.findCached(any(), any(), any(), anyBoolean())).thenReturn(cached);

        assertEquals(new AiTaskRuntime.Created("cached-task", AiTaskRuntime.State.QUEUED),
                service.create("token", "key", request("media-1")));

        verifyNoInteractions(tasks);
    }

    private static PhotoEvaluationService.Request request(String mediaId) {
        PhotoEvaluationService.Request request = new PhotoEvaluationService.Request();
        request.mediaId = mediaId;
        return request;
    }
}
