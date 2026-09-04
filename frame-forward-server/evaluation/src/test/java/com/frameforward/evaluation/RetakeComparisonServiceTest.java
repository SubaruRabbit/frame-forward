package com.frameforward.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.RetakeComparisonGraph;
import com.frameforward.auth.AuthService;
import com.frameforward.media.MediaEntity;
import com.frameforward.media.MediaManager;

class RetakeComparisonServiceTest {
    private final AuthService auth = mock(AuthService.class);
    private final PhotoEvaluationMapper evaluations = mock(PhotoEvaluationMapper.class);
    private final RetakeLinkMapper links = mock(RetakeLinkMapper.class);
    private final ShootingSessionMapper sessions = mock(ShootingSessionMapper.class);
    private final MediaManager media = mock(MediaManager.class);
    private final RetakeComparisonGraph graph = mock(RetakeComparisonGraph.class);
    private final RetakeComparisonService service = new RetakeComparisonService(auth, evaluations, links, sessions,
            media, graph, new ObjectMapper());

    @Test
    void createsComparisonAndPersistsRetakeLink() {
        arrangeComparableEvaluations();
        when(links.selectById("retake")).thenReturn(null);

        Map<String, Object> result = service.create("token", request());

        assertEquals(15, result.get("scoreDelta"));
        assertEquals("generated", result.get("summary"));
        ArgumentCaptor<RetakeLinkEntity> link = ArgumentCaptor.forClass(RetakeLinkEntity.class);
        verify(links).insert(link.capture());
        assertEquals("retake", link.getValue().retakeEvaluationId);
        assertEquals("original", link.getValue().originalEvaluationId);
        assertEquals("account-1", link.getValue().accountId);
        assertEquals("session-1", link.getValue().sessionId);
    }

    @Test
    void rejectsRequestForMissingOriginalEvaluation() {
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(evaluations.selectOne(any())).thenReturn(null);

        assertThrows(RetakeComparisonService.NotFound.class, () -> service.create("token", request()));

        verifyNoInteractions(links, sessions, media, graph);
    }

    @Test
    void returnsComparisonWithoutDuplicatingExistingRetakeLink() {
        arrangeComparableEvaluations();
        when(links.selectById("retake")).thenReturn(new RetakeLinkEntity());

        assertEquals(15, service.create("token", request()).get("scoreDelta"));

        verify(links, never()).insert(any(RetakeLinkEntity.class));
    }

    private void arrangeComparableEvaluations() {
        PhotoEvaluationEntity original = evaluation("original", "media-original",
                "{\"total\":70,\"dimensions\":{\"composition\":60}}");
        PhotoEvaluationEntity retake = evaluation("retake", "media-retake",
                "{\"total\":85,\"dimensions\":{\"composition\":80}}");
        ShootingSessionEntity session = new ShootingSessionEntity();
        session.planContext = "composition-plan";
        MediaEntity photo = new MediaEntity();
        photo.exifJson = "{}";
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(evaluations.selectOne(any())).thenReturn(original, retake);
        when(sessions.selectOne(any())).thenReturn(session);
        when(sessions.selectById("session-1")).thenReturn(session);
        when(media.findById(any())).thenReturn(photo);
        when(graph.summarize(any(), any())).thenReturn(Map.of("summary", "generated"));
    }

    private static PhotoEvaluationEntity evaluation(String id, String mediaId, String resultJson) {
        PhotoEvaluationEntity evaluation = new PhotoEvaluationEntity();
        evaluation.id = id;
        evaluation.mediaId = mediaId;
        evaluation.sessionId = "session-1";
        evaluation.resultJson = resultJson;
        return evaluation;
    }

    private static RetakeComparisonService.Request request() {
        RetakeComparisonService.Request request = new RetakeComparisonService.Request();
        request.originalEvaluationId = "original";
        request.retakeEvaluationId = "retake";
        return request;
    }
}
