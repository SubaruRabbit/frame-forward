package com.frameforward.generation;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.frameforward.ai.model.dto.AiTaskCompletionContext;
import com.frameforward.generation.business.InvalidRequest;
import com.frameforward.generation.business.ReferenceImageBusiness;
import com.frameforward.generation.manager.ReferenceImageManager;
import com.frameforward.generation.mapper.ReferenceImageMapper;
import com.frameforward.generation.model.dto.NewReference;
import com.frameforward.generation.model.dto.ReferenceImageRequest;
import com.frameforward.generation.model.entity.ReferenceImageEntity;
import com.frameforward.media.model.dto.MediaResponse;
import com.frameforward.media.service.MediaService;
import com.frameforward.shooting.mapper.ShootingPlanMapper;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;

class ReferenceImageLayerTest {
    @Test
    void managerPersistsOnlyOnceAndCompletesOnlyPendingReference() {
        var plans = mock(ShootingPlanMapper.class);
        var references = mock(ReferenceImageMapper.class);
        var media = mock(MediaService.class);
        var manager = new ReferenceImageManager(
                new com.frameforward.generation.repository.ReferenceImageRepository(plans, references));
        var processor = new com.frameforward.generation.service.ReferenceImageCompletionProcessor(manager, media);
        when(plans.selectOne(any())).thenReturn(new ShootingPlanEntity());
        assertNotNull(manager.findOwnedPlan("account", "plan"));
        when(references.selectOne(any())).thenReturn(null);
        var source = new NewReference("account", "environment", "plan", "task", "label", "prompt");
        manager.persistReferenceIfAbsent(source);
        verify(references).insert(any(ReferenceImageEntity.class));
        when(references.selectOne(any())).thenReturn(new ReferenceImageEntity());
        manager.persistReferenceIfAbsent(source);
        verify(references, times(1)).insert(any(ReferenceImageEntity.class));
        when(references.selectOne(any())).thenReturn(null);
        var task = new AiTaskCompletionContext("task", "account");
        processor.complete(task, new HashMap<>());
        verifyNoInteractions(media);
        var reference = new ReferenceImageEntity();
        reference.id = "reference";
        when(references.selectOne(any())).thenReturn(reference);
        var generated = new MediaResponse("media", 10, 20, "url", "original", "ai-copy");
        when(media.registerGenerated("account", "url", 10, 20)).thenReturn(generated);
        var result = new HashMap<String, Object>(Map.of("imageUrl", "url", "width", 10, "height", 20));
        processor.complete(task, result);
        assertEquals("media", reference.generatedMediaId);
        assertEquals("reference", result.get("referenceImageId"));
        verify(references).updateById(reference);
        processor.complete(task, result);
        verify(media, times(1)).registerGenerated("account", "url", 10, 20);
    }

    @Test
    void businessValidatesPlansBuildsControlledPromptAndDelegatesPersistence() {
        var manager = mock(ReferenceImageManager.class);
        var business = new ReferenceImageBusiness(manager);
        var request = new ReferenceImageRequest();
        request.environmentMediaId = "media";
        request.shootingPlanId = "plan";
        request.selectedPlan = plan();
        business.validate(request);
        request.selectedPlan = Map.of();
        assertThrows(InvalidRequest.class, () -> business.validate(request));
        request.selectedPlan = plan();
        assertTrue(business.controlledPrompt(request.selectedPlan).contains("机位：front"));
        var plan = new ShootingPlanEntity();
        when(manager.findOwnedPlan("account", "plan")).thenReturn(plan);
        assertSame(plan, business.findOwnedPlan("account", "plan"));
        business.persistReference("account", "media", "plan", "task", request.selectedPlan, "prompt");
        var saved = ArgumentCaptor.forClass(NewReference.class);
        verify(manager).persistReferenceIfAbsent(saved.capture());
        assertEquals("label", saved.getValue().selectedPlanLabel());
    }

    private static Map<String, Object> plan() {
        return Map.of("label", "label", "position", "front", "cameraHeight", "eye", "composition", "close",
                "orientation", "portrait", "focalLengthMm", 50, "exposure", Map.of());
    }
}
