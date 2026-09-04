package com.frameforward.evaluation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.frameforward.media.MediaEntity;

class EvaluationLayerTest {
    @Test
    void managerCoversCacheSessionPersistenceAndRetakeIdempotency() {
        var evaluations = mock(PhotoEvaluationMapper.class);
        var sessions = mock(ShootingSessionMapper.class);
        var links = mock(RetakeLinkMapper.class);
        var manager = new EvaluationManager(evaluations, sessions, links);
        var cached = new PhotoEvaluationEntity();
        when(evaluations.selectOne(any())).thenReturn(cached);
        assertNull(manager.findCached("account", "hash", null, true));
        assertSame(cached, manager.findCached("account", "hash", null, false));
        assertSame(cached, manager.findCached("account", "hash", "session", false));
        when(sessions.selectOne(any())).thenReturn(new ShootingSessionEntity());
        assertTrue(manager.hasOwnedSession("account", "session"));
        manager.save(cached);
        assertSame(cached, manager.findByTaskId("task"));
        manager.update(cached);
        assertSame(cached, manager.findOwnedEvaluation("account", "evaluation"));
        when(sessions.selectById("session")).thenReturn(new ShootingSessionEntity());
        assertNotNull(manager.findSession("session"));
        when(links.selectOne(any())).thenReturn(new RetakeLinkEntity());
        assertNotNull(manager.findOwnedRetake("account", "retake"));
        var original = new PhotoEvaluationEntity();
        original.id = "original";
        original.sessionId = "session";
        var retake = new PhotoEvaluationEntity();
        retake.id = "retake";
        when(links.selectById("retake")).thenReturn(null);
        manager.saveRetakeIfAbsent("account", original, retake);
        verify(links).insert(any(RetakeLinkEntity.class));
        when(links.selectById("retake")).thenReturn(new RetakeLinkEntity());
        manager.saveRetakeIfAbsent("account", original, retake);
        verify(links, times(1)).insert(any(RetakeLinkEntity.class));
    }

    @Test
    void businessValidatesSessionsAndPersistsOnlyNewResults() {
        var manager = mock(EvaluationManager.class);
        var business = new EvaluationBusiness(manager);
        var request = new PhotoEvaluationService.Request();
        request.mediaId = "media";
        request.sessionId = "session";
        var photo = new MediaEntity();
        photo.id = "media";
        photo.contentHash = "hash";
        business.validate(request);
        assertThrows(PhotoEvaluationService.Invalid.class, () -> business.validate(null));
        request.mediaId = " ";
        assertThrows(PhotoEvaluationService.Invalid.class, () -> business.validate(request));
        request.mediaId = "media";
        when(manager.hasOwnedSession("account", "session")).thenReturn(false);
        assertThrows(PhotoEvaluationService.NotFound.class, () -> business.requireOwnedSession("account", "session"));
        business.requireOwnedSession("account", " ");
        when(manager.hasOwnedSession("account", "session")).thenReturn(true);
        business.requireOwnedSession("account", "session");
        var cached = new PhotoEvaluationEntity();
        when(manager.findCached("account", "hash", "session", false)).thenReturn(cached);
        assertSame(cached, business.cached("account", photo, request));
        business.save("account", photo, request, "task");
        var saved = ArgumentCaptor.forClass(PhotoEvaluationEntity.class);
        verify(manager).save(saved.capture());
        assertEquals("v1", saved.getValue().executionVersion);
        request.reanalyze = true;
        business.save("account", photo, request, "task2");
        verify(manager, times(2)).save(any(PhotoEvaluationEntity.class));
        when(manager.findByTaskId("task")).thenReturn(null);
        business.saveResult("task", "{}");
        var result = new PhotoEvaluationEntity();
        when(manager.findByTaskId("task")).thenReturn(result);
        business.saveResult("task", "{}");
        assertEquals("{}", result.resultJson);
        verify(manager).update(result);
        business.saveResult("task", "other");
        verify(manager, times(1)).update(result);
    }
}
