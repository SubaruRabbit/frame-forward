package com.frameforward.generation;

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

import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.media.MediaEntity;
import com.frameforward.media.MediaMapper;
import com.frameforward.shooting.ShootingPlanEntity;
import com.frameforward.shooting.ShootingPlanMapper;

class ReferenceImageServiceTest {
    private final AuthService auth = mock(AuthService.class);
    private final MediaMapper media = mock(MediaMapper.class);
    private final ShootingPlanMapper plans = mock(ShootingPlanMapper.class);
    private final ReferenceImageMapper references = mock(ReferenceImageMapper.class);
    private final AiTaskRuntime tasks = mock(AiTaskRuntime.class);
    private final ReferenceImageService service = new ReferenceImageService(auth, media, plans, references, tasks);

    @Test
    void createsReferenceImageForValidRequest() {
        ReferenceImageService.Request request = validRequest();
        MediaEntity scene = new MediaEntity();
        scene.id = "media-1";
        ShootingPlanEntity plan = new ShootingPlanEntity();
        plan.id = "plan-1";
        AiTaskRuntime.Created created = new AiTaskRuntime.Created("task-1", AiTaskRuntime.State.QUEUED);
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.selectOne(any())).thenReturn(scene);
        when(plans.selectOne(any())).thenReturn(plan);
        when(tasks.create(any(), any(), any())).thenReturn(created);
        when(references.selectOne(any())).thenReturn(null);

        assertEquals(created, service.create("token", "key", request));

        ArgumentCaptor<ReferenceImageEntity> entity = ArgumentCaptor.forClass(ReferenceImageEntity.class);
        verify(references).insert(entity.capture());
        assertEquals("account-1", entity.getValue().accountId);
        assertEquals("media-1", entity.getValue().environmentMediaId);
        assertEquals("plan-1", entity.getValue().shootingPlanId);
        assertEquals("task-1", entity.getValue().aiTaskId);
        assertEquals("SAFE", entity.getValue().selectedPlanLabel);
        assertTrue(entity.getValue().promptText.contains("焦段：35mm"));
    }

    @ParameterizedTest
    @MethodSource("requestsMissingRequiredFields")
    void rejectsRequestsMissingRequiredFields(ReferenceImageService.Request request) {
        assertThrows(ReferenceImageService.InvalidRequest.class, () -> service.create("token", "key", request));
        verifyNoInteractions(auth, media, plans, references, tasks);
    }

    @ParameterizedTest
    @MethodSource("requestsWithInvalidStructuredFields")
    void rejectsInvalidStructuredPlanFields(ReferenceImageService.Request request) {
        assertThrows(ReferenceImageService.InvalidRequest.class, () -> service.create("token", "key", request));
        verifyNoInteractions(auth, media, plans, references, tasks);
    }

    private static Stream<ReferenceImageService.Request> requestsMissingRequiredFields() {
        ReferenceImageService.Request blankMedia = validRequest();
        blankMedia.environmentMediaId = " ";
        ReferenceImageService.Request missingPlan = validRequest();
        missingPlan.shootingPlanId = null;
        ReferenceImageService.Request missingSelectedPlan = validRequest();
        missingSelectedPlan.selectedPlan = null;
        ReferenceImageService.Request missingLabel = validRequest();
        missingLabel.selectedPlan.remove("label");
        ReferenceImageService.Request blankPosition = validRequest();
        blankPosition.selectedPlan.put("position", " ");
        return Stream.of(null, blankMedia, missingPlan, missingSelectedPlan, missingLabel, blankPosition);
    }

    private static Stream<ReferenceImageService.Request> requestsWithInvalidStructuredFields() {
        ReferenceImageService.Request focalLengthAsText = validRequest();
        focalLengthAsText.selectedPlan.put("focalLengthMm", "35");
        ReferenceImageService.Request exposureAsText = validRequest();
        exposureAsText.selectedPlan.put("exposure", "f/2.8");
        return Stream.of(focalLengthAsText, exposureAsText);
    }

    private static ReferenceImageService.Request validRequest() {
        ReferenceImageService.Request request = new ReferenceImageService.Request();
        request.environmentMediaId = "media-1";
        request.shootingPlanId = "plan-1";
        request.selectedPlan = new LinkedHashMap<>(
                Map.of("label", "SAFE", "position", "人行道内侧", "cameraHeight", "胸口高度", "composition", "引导线",
                        "orientation", "竖构图", "focalLengthMm", 35, "exposure", Map.of("aperture", "f/2.8")));
        return request;
    }
}
