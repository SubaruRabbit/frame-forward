package com.frameforward.shooting;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.equipment.UserEquipmentService;
import com.frameforward.media.MediaManager;

class SceneAnalysisServiceTest {
    @Test
    void acceptsCompleteRequestBeforeCheckingMediaOwnership() {
        var auth = mock(AuthService.class);
        when(auth.requireAccountId("token")).thenReturn("account-1");
        var service = service(auth);

        assertThrows(SceneAnalysisService.MediaNotOwned.class, () -> service.create("token", "request-1", request(1)));

        verify(auth).requireAccountId("token");
    }

    @Test
    void rejectsMissingRequiredFieldWithoutAuthenticating() {
        var auth = mock(AuthService.class);
        var request = request(30);
        request.subject = " ";

        assertThrows(SceneAnalysisService.InvalidRequest.class,
                () -> service(auth).create("token", "request-1", request));

        verifyNoInteractions(auth);
    }

    @Test
    void rejectsTimeConstraintOutsideSupportedBounds() {
        var service = service(mock(AuthService.class));

        assertThrows(SceneAnalysisService.InvalidRequest.class, () -> service.create("token", "request-1", request(0)));
        assertThrows(SceneAnalysisService.InvalidRequest.class,
                () -> service.create("token", "request-1", request(1441)));
    }

    private static SceneAnalysisService service(AuthService auth) {
        ShootingBusiness business = new ShootingBusiness(mock(ShootingManager.class), new ObjectMapper());
        return new SceneAnalysisService(auth, mock(MediaManager.class), mock(UserEquipmentService.class),
                mock(AiTaskRuntime.class), business);
    }

    private static SceneAnalysisService.Request request(int timeConstraintMinutes) {
        var request = new SceneAnalysisService.Request();
        request.environmentMediaId = "media-1";
        request.subjectType = "PORTRAIT";
        request.subject = "人物";
        request.targetStyle = "自然";
        request.timeConstraintMinutes = timeConstraintMinutes;
        request.equipmentIds = List.of();
        return request;
    }
}
