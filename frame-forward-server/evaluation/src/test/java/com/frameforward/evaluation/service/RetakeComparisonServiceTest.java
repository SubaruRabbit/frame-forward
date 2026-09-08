package com.frameforward.evaluation.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.business.RetakeComparisonGraph;
import com.frameforward.auth.service.AuthService;
import com.frameforward.evaluation.business.RetakeComparisonNotFound;
import com.frameforward.evaluation.manager.EvaluationManager;
import com.frameforward.evaluation.model.dto.RetakeComparisonRequest;
import com.frameforward.evaluation.model.entity.PhotoEvaluationEntity;
import com.frameforward.evaluation.model.entity.ShootingSessionEntity;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.entity.MediaEntity;

class RetakeComparisonServiceTest {
    private final AuthService auth = mock(AuthService.class);
    private final EvaluationManager manager = mock(EvaluationManager.class);
    private final MediaManager media = mock(MediaManager.class);
    private final RetakeComparisonGraph graph = mock(RetakeComparisonGraph.class);
    private final RetakeComparisonService service = new RetakeComparisonService(auth, manager, media, graph,
            new ObjectMapper());

    @Test
    void createsComparisonAndPersistsRetakeLink() {
        arrangeComparableEvaluations();
        when(manager.findOwnedEvaluation(any(), any())).thenReturn(
                evaluation("original", "media-original", "{\"total\":70,\"dimensions\":{\"composition\":60}}"),
                evaluation("retake", "media-retake", "{\"total\":85,\"dimensions\":{\"composition\":80}}"));
        when(manager.hasOwnedSession(any(), any())).thenReturn(true);
        when(manager.findSession("session-1")).thenReturn(session());

        Map<String, Object> result = service.create("token", request());

        assertEquals(15, result.get("scoreDelta"));
        assertEquals("generated", result.get("summary"));
        verify(manager).saveRetakeIfAbsent(any(), any(), any());
    }

    @Test
    void rejectsRequestForMissingOriginalEvaluation() {
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(manager.findOwnedEvaluation(any(), any())).thenReturn(null);

        assertThrows(RetakeComparisonNotFound.class, () -> service.create("token", request()));

        verifyNoInteractions(media, graph);
    }

    @Test
    void returnsComparisonWithoutDuplicatingExistingRetakeLink() {
        arrangeComparableEvaluations();
        assertEquals(15, service.create("token", request()).get("scoreDelta"));
        verify(manager).saveRetakeIfAbsent(any(), any(), any());
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
        when(manager.findOwnedEvaluation(any(), any())).thenReturn(original, retake);
        when(manager.hasOwnedSession(any(), any())).thenReturn(true);
        when(manager.findSession("session-1")).thenReturn(session);
        when(media.findEntityById(any())).thenReturn(photo);
        when(graph.summarize(any(), any())).thenReturn(Map.of("summary", "generated"));
    }

    private static ShootingSessionEntity session() {
        ShootingSessionEntity value = new ShootingSessionEntity();
        value.planContext = "composition-plan";
        return value;
    }

    private static PhotoEvaluationEntity evaluation(String id, String mediaId, String resultJson) {
        PhotoEvaluationEntity evaluation = new PhotoEvaluationEntity();
        evaluation.id = id;
        evaluation.mediaId = mediaId;
        evaluation.sessionId = "session-1";
        evaluation.resultJson = resultJson;
        return evaluation;
    }

    private static RetakeComparisonRequest request() {
        RetakeComparisonRequest request = new RetakeComparisonRequest();
        request.originalEvaluationId = "original";
        request.retakeEvaluationId = "retake";
        return request;
    }
}
