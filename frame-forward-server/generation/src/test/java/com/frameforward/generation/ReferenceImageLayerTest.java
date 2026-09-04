package com.frameforward.generation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.frameforward.ai.AiTaskEntity;
import com.frameforward.media.MediaService;
import com.frameforward.shooting.ShootingPlanEntity;
import com.frameforward.shooting.ShootingPlanMapper;

class ReferenceImageLayerTest {
    @Test
    void managerPersistsOnlyOnceAndCompletesOnlyPendingReference() {
        var plans = mock(ShootingPlanMapper.class);
        var references = mock(ReferenceImageMapper.class);
        var media = mock(MediaService.class);
        var manager = new ReferenceImageManager(plans, references, media);
        when(plans.selectOne(any())).thenReturn(new ShootingPlanEntity());
        assertNotNull(manager.findOwnedPlan("account", "plan"));
        when(references.selectOne(any())).thenReturn(null);
        var source = new ReferenceImageManager.NewReference("account", "environment", "plan", "task", "label",
                "prompt");
        manager.persistReferenceIfAbsent(source);
        verify(references).insert(any(ReferenceImageEntity.class));
        when(references.selectOne(any())).thenReturn(new ReferenceImageEntity());
        manager.persistReferenceIfAbsent(source);
        verify(references, times(1)).insert(any(ReferenceImageEntity.class));
        when(references.selectOne(any())).thenReturn(null);
        var task = new AiTaskEntity();
        task.id = "task";
        task.accountId = "account";
        manager.complete(task, new HashMap<>());
        verifyNoInteractions(media);
        var reference = new ReferenceImageEntity();
        reference.id = "reference";
        when(references.selectOne(any())).thenReturn(reference);
        var generated = new MediaService.MediaResponse("media", 10, 20, "url", "original", "ai-copy");
        when(media.registerGenerated("account", "url", 10, 20)).thenReturn(generated);
        var result = new HashMap<String, Object>(Map.of("imageUrl", "url", "width", 10, "height", 20));
        manager.complete(task, result);
        assertEquals("media", reference.generatedMediaId);
        assertEquals("reference", result.get("referenceImageId"));
        verify(references).updateById(reference);
        manager.complete(task, result);
        verify(media, times(1)).registerGenerated("account", "url", 10, 20);
    }

    @Test
    void businessValidatesPlansBuildsControlledPromptAndDelegatesPersistence() {
        var manager = mock(ReferenceImageManager.class);
        var business = new ReferenceImageBusiness(manager);
        var request = new ReferenceImageService.Request();
        request.environmentMediaId = "media";
        request.shootingPlanId = "plan";
        request.selectedPlan = plan();
        business.validate(request);
        request.selectedPlan = Map.of();
        assertThrows(ReferenceImageService.InvalidRequest.class, () -> business.validate(request));
        request.selectedPlan = plan();
        assertTrue(business.controlledPrompt(request.selectedPlan).contains("机位：front"));
        var plan = new ShootingPlanEntity();
        when(manager.findOwnedPlan("account", "plan")).thenReturn(plan);
        assertSame(plan, business.findOwnedPlan("account", "plan"));
        business.persistReference("account", "media", "plan", "task", request.selectedPlan, "prompt");
        var saved = ArgumentCaptor.forClass(ReferenceImageManager.NewReference.class);
        verify(manager).persistReferenceIfAbsent(saved.capture());
        assertEquals("label", saved.getValue().selectedPlanLabel());
    }

    private static Map<String, Object> plan() {
        return Map.of("label", "label", "position", "front", "cameraHeight", "eye", "composition", "close",
                "orientation", "portrait", "focalLengthMm", 50, "exposure", Map.of());
    }
}
