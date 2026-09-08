package com.frameforward.media.manager;
import java.util.List;

import org.springframework.stereotype.Component;

import com.frameforward.media.model.entity.MediaEntity;
import com.frameforward.media.repository.MediaRepository;
@Component
public class MediaManager {
    private final MediaRepository repository;
    public MediaManager(MediaRepository repository) {
        this.repository = repository;
    }
    public MediaEntity findExisting(String ownerId, String contentHash) {
        return repository.findExisting(ownerId, contentHash);
    }
    public MediaEntity findOwnedEntity(String ownerId, String mediaId) {
        return repository.findOwned(ownerId, mediaId);
    }
    public MediaEntity findEntityById(String mediaId) {
        return repository.findById(mediaId);
    }
    public List<MediaEntity> listOwned(String ownerId) {
        return repository.listOwned(ownerId);
    }
    public List<MediaEntity> listOwnedDescending(String ownerId) {
        return repository.listOwnedDescending(ownerId);
    }
    public void save(MediaEntity entity) {
        repository.save(entity);
    }
    public void delete(String mediaId) {
        repository.delete(mediaId);
    }
}
