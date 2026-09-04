package com.frameforward.media;

import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/** 统一协调媒体持久化访问，避免业务服务直接依赖 Mapper。 */
@Component
public class MediaManager {
    private final MediaMapper media;

    public MediaManager(MediaMapper media) {
        this.media = media;
    }

    public MediaEntity findExisting(String ownerId, String contentHash) {
        return media.selectOne(new LambdaQueryWrapper<MediaEntity>().eq(MediaEntity::getOwnerId, ownerId)
                .eq(MediaEntity::getContentHash, contentHash));
    }

    public MediaEntity findOwned(String ownerId, String mediaId) {
        return media.selectOne(new LambdaQueryWrapper<MediaEntity>().eq(MediaEntity::getId, mediaId)
                .eq(MediaEntity::getOwnerId, ownerId));
    }

    public MediaEntity findById(String mediaId) {
        return media.selectById(mediaId);
    }

    public List<MediaEntity> listOwned(String ownerId) {
        return media.selectList(new LambdaQueryWrapper<MediaEntity>().eq(MediaEntity::getOwnerId, ownerId));
    }

    public List<MediaEntity> listOwnedDescending(String ownerId) {
        return media.selectList(new LambdaQueryWrapper<MediaEntity>().eq(MediaEntity::getOwnerId, ownerId)
                .orderByDesc(MediaEntity::getId));
    }

    public void save(MediaEntity entity) {
        media.insert(entity);
    }

    public void delete(String mediaId) {
        media.deleteById(mediaId);
    }
}
