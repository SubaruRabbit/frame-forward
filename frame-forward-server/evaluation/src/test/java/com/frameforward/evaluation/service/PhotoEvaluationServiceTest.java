package com.frameforward.evaluation.service;
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
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskState;
import com.frameforward.ai.service.AiTaskRuntime;
import com.frameforward.auth.service.AuthService;
import com.frameforward.evaluation.business.EvaluationBusiness;
import com.frameforward.evaluation.business.PhotoEvaluationNotFound;
import com.frameforward.evaluation.manager.EvaluationManager;
import com.frameforward.evaluation.model.dto.PhotoEvaluationRequest;
import com.frameforward.evaluation.model.entity.PhotoEvaluationEntity;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.entity.MediaEntity;

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
        AiTaskCreated created = new AiTaskCreated("task-1", AiTaskState.QUEUED);
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.findOwnedEntity("account-1", "media-1")).thenReturn(photo);
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
        when(media.findOwnedEntity("account-1", "missing")).thenReturn(null);

        assertThrows(PhotoEvaluationNotFound.class, () -> service.create("token", "key", request("missing")));

        verifyNoInteractions(manager, tasks);
    }

    @Test
    void reusesCachedTaskWhenReanalysisIsNotRequested() {
        MediaEntity photo = new MediaEntity();
        photo.contentHash = "hash-1";
        PhotoEvaluationEntity cached = new PhotoEvaluationEntity();
        cached.aiTaskId = "cached-task";
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.findOwnedEntity("account-1", "media-1")).thenReturn(photo);
        when(manager.findCached(any(), any(), any(), anyBoolean())).thenReturn(cached);

        assertEquals(new AiTaskCreated("cached-task", AiTaskState.QUEUED),
                service.create("token", "key", request("media-1")));

        verifyNoInteractions(tasks);
    }

    private static PhotoEvaluationRequest request(String mediaId) {
        PhotoEvaluationRequest request = new PhotoEvaluationRequest();
        request.mediaId = mediaId;
        return request;
    }
}
