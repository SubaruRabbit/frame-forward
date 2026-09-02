package com.frameforward.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import com.frameforward.media.MediaMapper;

class PhotoEvaluationServiceTest {
    private final AuthService auth = mock(AuthService.class);
    private final MediaMapper media = mock(MediaMapper.class);
    private final PhotoEvaluationMapper evaluations = mock(PhotoEvaluationMapper.class);
    private final ShootingSessionMapper sessions = mock(ShootingSessionMapper.class);
    private final AiTaskRuntime tasks = mock(AiTaskRuntime.class);
    private final PhotoEvaluationService service = new PhotoEvaluationService(auth, media, evaluations, sessions, tasks,
            new ObjectMapper());

    @Test
    void createsEvaluationAndPersistsItsTaskForNewPhoto() {
        MediaEntity photo = new MediaEntity();
        photo.id = "media-1";
        photo.contentHash = "hash-1";
        photo.exifJson = "{}";
        AiTaskRuntime.Created created = new AiTaskRuntime.Created("task-1", AiTaskRuntime.State.QUEUED);
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.selectOne(any())).thenReturn(photo);
        when(evaluations.selectOne(any())).thenReturn(null);
        when(tasks.create(any(), any(), any())).thenReturn(created);

        assertEquals(created, service.create("token", "key", request("media-1")));

        ArgumentCaptor<PhotoEvaluationEntity> entity = ArgumentCaptor.forClass(PhotoEvaluationEntity.class);
        verify(evaluations).insert(entity.capture());
        assertEquals("account-1", entity.getValue().accountId);
        assertEquals("media-1", entity.getValue().mediaId);
        assertEquals("hash-1", entity.getValue().contentHash);
        assertEquals("task-1", entity.getValue().aiTaskId);
    }

    @Test
    void rejectsRequestForMissingPhoto() {
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.selectOne(any())).thenReturn(null);

        assertThrows(PhotoEvaluationService.NotFound.class, () -> service.create("token", "key", request("missing")));

        verifyNoInteractions(evaluations, sessions, tasks);
    }

    @Test
    void reusesCachedTaskWhenReanalysisIsNotRequested() {
        MediaEntity photo = new MediaEntity();
        photo.contentHash = "hash-1";
        PhotoEvaluationEntity cached = new PhotoEvaluationEntity();
        cached.aiTaskId = "cached-task";
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.selectOne(any())).thenReturn(photo);
        when(evaluations.selectOne(any())).thenReturn(cached);

        assertEquals(new AiTaskRuntime.Created("cached-task", AiTaskRuntime.State.QUEUED),
                service.create("token", "key", request("media-1")));

        verifyNoInteractions(sessions, tasks);
    }

    private static PhotoEvaluationService.Request request(String mediaId) {
        PhotoEvaluationService.Request request = new PhotoEvaluationService.Request();
        request.mediaId = mediaId;
        return request;
    }
}
