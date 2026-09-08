package com.frameforward.generation.manager;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.frameforward.generation.model.dto.NewReference;
import com.frameforward.generation.model.entity.ReferenceImageEntity;
import com.frameforward.generation.repository.ReferenceImageRepository;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;

@Component
public class ReferenceImageManager {
    private final ReferenceImageRepository repository;

    public ReferenceImageManager(ReferenceImageRepository repository) {
        this.repository = repository;
    }

    public ShootingPlanEntity findOwnedPlan(String accountId, String planId) {
        return repository.findOwnedPlan(accountId, planId);
    }

    public void persistReferenceIfAbsent(NewReference source) {
        if (repository.findByTask(source.aiTaskId()) != null) {
            return;
        }
        ReferenceImageEntity reference = new ReferenceImageEntity();
        reference.id = UUID.randomUUID().toString();
        reference.accountId = source.accountId();
        reference.environmentMediaId = source.environmentMediaId();
        reference.shootingPlanId = source.shootingPlanId();
        reference.aiTaskId = source.aiTaskId();
        reference.selectedPlanLabel = source.selectedPlanLabel();
        reference.promptText = source.promptText();
        reference.createdAt = Instant.now();
        repository.save(reference);
    }

    public ReferenceImageEntity findByTask(String taskId) {
        return repository.findByTask(taskId);
    }
    public void saveGeneratedMedia(ReferenceImageEntity reference, String mediaId) {
        reference.generatedMediaId = mediaId;
        repository.update(reference);
    }

}
