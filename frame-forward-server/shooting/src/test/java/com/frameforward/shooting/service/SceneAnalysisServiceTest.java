package com.frameforward.shooting.service;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.service.AiTaskRuntime;
import com.frameforward.auth.service.AuthService;
import com.frameforward.equipment.service.UserEquipmentService;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.shooting.business.SceneAnalysisInvalidRequest;
import com.frameforward.shooting.business.SceneAnalysisMediaNotOwned;
import com.frameforward.shooting.business.ShootingBusiness;
import com.frameforward.shooting.manager.ShootingManager;
import com.frameforward.shooting.model.dto.SceneAnalysisRequest;

class SceneAnalysisServiceTest {
    @Test
    void acceptsCompleteRequestBeforeCheckingMediaOwnership() {
        var auth = mock(AuthService.class);
        when(auth.requireAccountId("token")).thenReturn("account-1");
        var service = service(auth);

        assertThrows(SceneAnalysisMediaNotOwned.class, () -> service.create("token", "request-1", request(1)));

        verify(auth).requireAccountId("token");
    }

    @Test
    void rejectsMissingRequiredFieldWithoutAuthenticating() {
        var auth = mock(AuthService.class);
        var request = request(30);
        request.subject = " ";

        assertThrows(SceneAnalysisInvalidRequest.class, () -> service(auth).create("token", "request-1", request));

        verifyNoInteractions(auth);
    }

    @Test
    void rejectsTimeConstraintOutsideSupportedBounds() {
        var service = service(mock(AuthService.class));

        assertThrows(SceneAnalysisInvalidRequest.class, () -> service.create("token", "request-1", request(0)));
        assertThrows(SceneAnalysisInvalidRequest.class, () -> service.create("token", "request-1", request(1441)));
    }

    private static SceneAnalysisService service(AuthService auth) {
        ShootingBusiness business = new ShootingBusiness(mock(ShootingManager.class), new ObjectMapper());
        return new SceneAnalysisService(auth, mock(MediaManager.class), mock(UserEquipmentService.class),
                mock(AiTaskRuntime.class), business);
    }

    private static SceneAnalysisRequest request(int timeConstraintMinutes) {
        var request = new SceneAnalysisRequest();
        request.environmentMediaId = "media-1";
        request.subjectType = "PORTRAIT";
        request.subject = "人物";
        request.targetStyle = "自然";
        request.timeConstraintMinutes = timeConstraintMinutes;
        request.equipmentIds = List.of();
        return request;
    }
}
