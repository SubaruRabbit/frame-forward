package com.frameforward.generation.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskState;
import com.frameforward.ai.service.AiTaskRuntime;
import com.frameforward.auth.service.AuthService;
import com.frameforward.generation.business.InvalidRequest;
import com.frameforward.generation.business.ReferenceImageBusiness;
import com.frameforward.generation.manager.ReferenceImageManager;
import com.frameforward.generation.model.dto.NewReference;
import com.frameforward.generation.model.dto.ReferenceImageRequest;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.entity.MediaEntity;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;

class ReferenceImageServiceTest {
    private final AuthService auth = mock(AuthService.class);
    private final MediaManager media = mock(MediaManager.class);
    private final AiTaskRuntime tasks = mock(AiTaskRuntime.class);
    private final ReferenceImageManager manager = mock(ReferenceImageManager.class);
    private final ReferenceImageService service = new ReferenceImageService(auth, media, tasks,
            new ReferenceImageBusiness(manager));

    @Test
    void createsReferenceImageForValidRequest() {
        ReferenceImageRequest request = validRequest();
        MediaEntity scene = new MediaEntity();
        scene.id = "media-1";
        ShootingPlanEntity plan = new ShootingPlanEntity();
        plan.id = "plan-1";
        AiTaskCreated created = new AiTaskCreated("task-1", AiTaskState.QUEUED);
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.findOwnedEntity("account-1", "media-1")).thenReturn(scene);
        when(manager.findOwnedPlan("account-1", "plan-1")).thenReturn(plan);
        when(tasks.create(any(), any(), any())).thenReturn(created);

        assertEquals(created, service.create("token", "key", request));

        ArgumentCaptor<NewReference> entity = ArgumentCaptor.forClass(NewReference.class);
        verify(manager).persistReferenceIfAbsent(entity.capture());
        assertEquals("account-1", entity.getValue().accountId());
        assertEquals("media-1", entity.getValue().environmentMediaId());
        assertEquals("plan-1", entity.getValue().shootingPlanId());
        assertEquals("task-1", entity.getValue().aiTaskId());
        assertEquals("SAFE", entity.getValue().selectedPlanLabel());
        assertTrue(entity.getValue().promptText().contains("焦段：35mm"));
    }

    @ParameterizedTest
    @MethodSource("requestsMissingRequiredFields")
    void rejectsRequestsMissingRequiredFields(ReferenceImageRequest request) {
        assertThrows(InvalidRequest.class, () -> service.create("token", "key", request));
        verifyNoInteractions(auth, media, tasks, manager);
    }

    @ParameterizedTest
    @MethodSource("requestsWithInvalidStructuredFields")
    void rejectsInvalidStructuredPlanFields(ReferenceImageRequest request) {
        assertThrows(InvalidRequest.class, () -> service.create("token", "key", request));
        verifyNoInteractions(auth, media, tasks, manager);
    }

    private static Stream<ReferenceImageRequest> requestsMissingRequiredFields() {
        ReferenceImageRequest blankMedia = validRequest();
        blankMedia.environmentMediaId = " ";
        ReferenceImageRequest missingPlan = validRequest();
        missingPlan.shootingPlanId = null;
        ReferenceImageRequest missingSelectedPlan = validRequest();
        missingSelectedPlan.selectedPlan = null;
        ReferenceImageRequest missingLabel = validRequest();
        missingLabel.selectedPlan.remove("label");
        ReferenceImageRequest blankPosition = validRequest();
        blankPosition.selectedPlan.put("position", " ");
        return Stream.of(null, blankMedia, missingPlan, missingSelectedPlan, missingLabel, blankPosition);
    }

    private static Stream<ReferenceImageRequest> requestsWithInvalidStructuredFields() {
        ReferenceImageRequest focalLengthAsText = validRequest();
        focalLengthAsText.selectedPlan.put("focalLengthMm", "35");
        ReferenceImageRequest exposureAsText = validRequest();
        exposureAsText.selectedPlan.put("exposure", "f/2.8");
        return Stream.of(focalLengthAsText, exposureAsText);
    }

    private static ReferenceImageRequest validRequest() {
        ReferenceImageRequest request = new ReferenceImageRequest();
        request.environmentMediaId = "media-1";
        request.shootingPlanId = "plan-1";
        request.selectedPlan = new LinkedHashMap<>(
                Map.of("label", "SAFE", "position", "人行道内侧", "cameraHeight", "胸口高度", "composition", "引导线",
                        "orientation", "竖构图", "focalLengthMm", 35, "exposure", Map.of("aperture", "f/2.8")));
        return request;
    }
}
